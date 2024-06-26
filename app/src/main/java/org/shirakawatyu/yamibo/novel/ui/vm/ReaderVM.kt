package org.shirakawatyu.yamibo.novel.ui.vm

import android.util.Log
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.shirakawatyu.yamibo.novel.bean.Content
import org.shirakawatyu.yamibo.novel.bean.ContentType
import org.shirakawatyu.yamibo.novel.bean.ReaderSettings
import org.shirakawatyu.yamibo.novel.constant.RequestConfig
import org.shirakawatyu.yamibo.novel.global.GlobalData
import org.shirakawatyu.yamibo.novel.ui.state.ReaderState
import org.shirakawatyu.yamibo.novel.util.FavoriteUtil
import org.shirakawatyu.yamibo.novel.util.HTMLUtil
import org.shirakawatyu.yamibo.novel.util.SettingsUtil
import org.shirakawatyu.yamibo.novel.util.TextUtil
import org.shirakawatyu.yamibo.novel.util.ValueUtil

class ReaderVM : ViewModel() {
    private val _uiState = MutableStateFlow(ReaderState())
    val uiState = _uiState.asStateFlow()

    private var pagerState: PagerState? = null
    private var pageEnd = true
    private var maxHeight = 0.dp
    private var maxWidth = 0.dp
    private var initialized = false
    private val logTag = "ReaderVM"
    private var compositionScope: CoroutineScope? = null
    var url by mutableStateOf("")
        private set
    var displayWebView by mutableStateOf(false)
        private set

    init {
        Log.i(logTag, "VM created.")
    }

    fun firstLoad(initUrl: String, initHeight: Dp, initWidth: Dp) {
        url = initUrl
        maxWidth = initWidth
        maxHeight = initHeight
        val setState = {
            FavoriteUtil.getFavoriteMap { it2 ->
                it2[url]?.let { it1 ->
                    Log.i(logTag, "first: $it1")
                    _uiState.value = _uiState.value.copy(currentView = it1.lastView)
                }
                displayWebView = true
            }
        }
        SettingsUtil.getSettings(callback = {
            _uiState.value = _uiState.value.copy(
                fontSize = ValueUtil.pxToSp(it.fontSizePx),
                lingHeight = ValueUtil.pxToSp(it.lineHeightPx),
                padding = it.padding
            )
            setState()
        }, onNull = setState)
    }

    fun loadFinished(html: String) {
        GlobalData.loading = true
        Thread {
            getContentByHTML(html)
            GlobalData.loading = false
            if (!initialized) {
                FavoriteUtil.getFavoriteMap {
                    it[url]?.let { it1 ->
                        CoroutineScope(Dispatchers.Main).launch {
                            while (pagerState == null) {
                                delay(100)
                            }
                            compositionScope?.launch {
                                pagerState?.animateScrollToPage(it1.lastPage)
                                initialized = true
                            }
                        }
                    }
                }
            } else {
                compositionScope?.launch {
                    pagerState?.scrollToPage(0)
                }
            }
            displayWebView = false
        }.start()
    }

    private fun getContentByHTML(html: String) {
        val doc = Jsoup.parse(html)
        doc.getElementsByTag("i").forEach { it.remove() }
        val passages = ArrayList<Content>()
        for (node in doc.getElementsByClass("message")) {
            val pagedText = TextUtil.pagingText(
                HTMLUtil.toText(node.html()),
                maxHeight - uiState.value.padding - ValueUtil.spToDp(uiState.value.lingHeight),
                maxWidth - uiState.value.padding * 2,
                uiState.value.fontSize,
                uiState.value.letterSpacing,
                uiState.value.lingHeight,
            )
            for (t in pagedText) {
                passages.add(Content(t, ContentType.TEXT))
            }
            for (element in node.getElementsByTag("img")) {
                val src = element.attribute("src").value
                passages.add(Content("${RequestConfig.BASE_URL}/${src}", ContentType.IMG))
            }
        }
        passages.add(Content("正在加载下一页", ContentType.TEXT))
        _uiState.value = ReaderState(passages, _uiState.value.currentView)
    }

    fun onPageChange(curPagerState: PagerState, scope: CoroutineScope) {
        var viewIndex = uiState.value.currentView
        if (pagerState == null) {
            pagerState = curPagerState
        }
        if (compositionScope == null) {
            compositionScope = scope
        }
        if (curPagerState.currentPage == curPagerState.targetPage &&
            curPagerState.currentPage == uiState.value.htmlList.size - 1 &&
            curPagerState.currentPage > 0 && pageEnd
        ) {
            viewIndex += 1
            displayWebView = true
            _uiState.value = _uiState.value.copy(currentView = viewIndex)
            pagerState = curPagerState
            pageEnd = false
        } else {
            pageEnd = true
        }
        saveHistory(curPagerState.targetPage)
        if (curPagerState.settledPage != curPagerState.targetPage && _uiState.value.scale != 1f) {
            _uiState.value = _uiState.value.copy(scale = 1f, offset = Offset(0f, 0f))
        }
    }

    private fun saveHistory(curPage: Int) {
        FavoriteUtil.getFavoriteMap {
            it[url]?.let { it1 ->
                FavoriteUtil.updateFavorite(
                    it1.copy(
                        lastPage = curPage,
                        lastView = uiState.value.currentView
                    )
                )
            }
        }
    }

    fun saveSettings() {
        val state = _uiState.value
        pagerState?.let {
            val settings = ReaderSettings(
                ValueUtil.spToPx(state.fontSize),
                ValueUtil.spToPx(state.lingHeight),
                state.padding
            )
            SettingsUtil.saveSettings(settings)
        }
    }

    fun onTransform(pan: Offset, zoom: Float) {
        val scale = (_uiState.value.scale * zoom).coerceIn(0.5f, 3f)
        val offset = if (scale == 1f) Offset(0f, 0f) else _uiState.value.offset + pan
        _uiState.value = _uiState.value.copy(scale = scale, offset = offset)
    }

    fun onSetView(view: Int) {
        _uiState.value = _uiState.value.copy(currentView = view)
        displayWebView = true
    }

    fun onSetPage(page: Int) {
        compositionScope?.launch {
            pagerState?.animateScrollToPage(page)
        }
    }

    fun onSetFontSize(fontSize: TextUnit) {
        _uiState.value = _uiState.value.copy(fontSize = fontSize)
    }

    fun onSetLineHeight(lineHeight: TextUnit) {
        _uiState.value = _uiState.value.copy(lingHeight = lineHeight)
    }

    fun onSetPadding(padding: Dp) {
        _uiState.value = _uiState.value.copy(padding = padding)
    }
}
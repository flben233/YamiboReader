package org.shirakawatyu.yamibo.novel.ui.vm

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.shirakawatyu.yamibo.novel.bean.Content
import org.shirakawatyu.yamibo.novel.bean.ContentType
import org.shirakawatyu.yamibo.novel.bean.ReaderSettings
import org.shirakawatyu.yamibo.novel.constant.RequestConfig
import org.shirakawatyu.yamibo.novel.ui.state.ReaderState
import org.shirakawatyu.yamibo.novel.util.FavoriteUtil
import org.shirakawatyu.yamibo.novel.util.HTMLUtil
import org.shirakawatyu.yamibo.novel.util.SettingsUtil
import org.shirakawatyu.yamibo.novel.util.TextUtil
import org.shirakawatyu.yamibo.novel.util.ValueUtil

class ReaderVM : ViewModel() {
    private val _uiState = MutableStateFlow(ReaderState())
    val uiState = _uiState.asStateFlow()

    @OptIn(ExperimentalFoundationApi::class)
    var pagerState: PagerState? = null
    private var pageEnd = true
    private var maxHeight = 0.dp
    private var maxWidth = 0.dp
    private var initialPage = 0
    var url by mutableStateOf("")
        private set
    var displayWebView by mutableStateOf(false)
        private set

    init {
        println("VM创建!")
    }

    fun firstLoad(initUrl: String, initHeight: Dp, initWidth: Dp) {
        url = initUrl
        maxWidth = initWidth
        maxHeight = initHeight
        SettingsUtil.getSettings(callback =  {
            _uiState.value = _uiState.value.copy(
                fontSize = ValueUtil.pxToSp(it.fontSizePx),
                lingHeight = ValueUtil.pxToSp(it.lineHeightPx),
                padding = it.padding
            )
            FavoriteUtil.getFavoriteMap {it2 ->
                it2[url]?.let { it1 ->
                    println("first: $it1")
                    _uiState.value = _uiState.value.copy(currentView = it1.lastView)
                }
                displayWebView = true
            }
        }, onNull = {
            FavoriteUtil.getFavoriteMap {it2 ->
                it2[url]?.let { it1 ->
                    println("first: $it1")
                    _uiState.value = _uiState.value.copy(currentView = it1.lastView)
                }
                displayWebView = true
            }
        })
    }

    fun loadFinished(html: String) {
        getContentByHTML(html)
        displayWebView = false
        FavoriteUtil.getFavoriteMap {
            it[url]?.let { it1 ->
                if (uiState.value.currentView == it1.lastView) {
                    initialPage = it1.lastPage
                }
            }
        }
        onSetPage(0)
    }

    fun jumpPage() {
        onSetPage(initialPage)
    }

    private fun getContentByHTML(html: String) {
        val doc = Jsoup.parse(html)
        val nodes = doc.getElementsByClass("message")
        val passages = ArrayList<Content>()
        for (node in nodes) {
            node.getElementsByTag("i").remove()
            val text = HTMLUtil.toText(node.html())
            val pagedText = TextUtil.pagingText(
                text,
                maxHeight - uiState.value.padding,
                maxWidth - uiState.value.padding,
                uiState.value.fontSize,
                uiState.value.lingHeight
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

    @OptIn(ExperimentalFoundationApi::class)
    fun onPageChange(curPagerState: PagerState) {
        var viewIndex = uiState.value.currentView
        if (pagerState == null) {
            pagerState = curPagerState
        }
        saveHistory(curPagerState.currentPage)
        if (curPagerState.currentPage == curPagerState.targetPage &&
            curPagerState.currentPage == uiState.value.htmlList.size - 1 &&
            curPagerState.currentPage > 0
        ) {
            if (pageEnd) {
                viewIndex += 1
                displayWebView = true
                _uiState.value = _uiState.value.copy(currentView = viewIndex)
                pagerState = curPagerState
                pageEnd = false
            }
        } else {
            pageEnd = true
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

    @OptIn(ExperimentalFoundationApi::class)
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

    fun onSetView(view: Int) {
        _uiState.value = _uiState.value.copy(currentView = view)
        displayWebView = true
    }

    @OptIn(ExperimentalFoundationApi::class)
    fun onSetPage(page: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            pagerState?.scrollToPage(page)
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
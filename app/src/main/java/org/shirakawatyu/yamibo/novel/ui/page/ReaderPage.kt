package org.shirakawatyu.yamibo.novel.ui.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import org.shirakawatyu.yamibo.novel.constant.RequestConfig
import org.shirakawatyu.yamibo.novel.ui.vm.ReaderVM
import org.shirakawatyu.yamibo.novel.ui.widget.ContentViewer
import org.shirakawatyu.yamibo.novel.ui.widget.NumInputWithConfirm
import org.shirakawatyu.yamibo.novel.ui.widget.PassageWebView


@Composable
fun ReaderPage(
    readerVM: ReaderVM = viewModel(),
    url: String = ""
) {
    val uiState by readerVM.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { uiState.htmlList.size })
    var showSettingDialog by remember { mutableStateOf(false) }
    val context = rememberCoroutineScope()

    BoxWithConstraints {
        LaunchedEffect(Unit) {
            readerVM.firstLoad(url, maxHeight, maxWidth)
        }
    }
    if (readerVM.displayWebView) {
        PassageWebView(url = "${RequestConfig.BASE_URL}/${url}&page=${uiState.currentView}") { html, _ ->
            readerVM.loadFinished(html)
        }
    } else {
        Box {
            if (showSettingDialog) {
                SettingDialog(
                    pageCount = pagerState.pageCount,
                    onDismissRequest = {
                        readerVM.saveSettings()
                        showSettingDialog = false
                    },
                    onSetPage = { readerVM.onSetPage(it) },
                    onSetFontSize = { readerVM.onSetFontSize(it) },
                    onSetPadding = { readerVM.onSetPadding(it) },
                    onSetView = { readerVM.onSetView(it) },
                    onSetLineHeight = { readerVM.onSetLineHeight(it) },
                    onJumpPage = { readerVM.jumpPage() }
                )
            }
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { showSettingDialog = true }
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            readerVM.onTransform(pan, zoom)
                        }
                    }
                    .graphicsLayer(
                        scaleX = uiState.scale,
                        scaleY = uiState.scale,
                        translationX = uiState.offset.x,
                        translationY = uiState.offset.y
                    ),
                state = pagerState
            ) { page ->
                ContentViewer(
                    data = uiState.htmlList[page],
                    padding = uiState.padding,
                    lineHeight = uiState.lingHeight,
                    letterSpacing = uiState.letterSpacing,
                    fontSize = uiState.fontSize,
                    currentPage = pagerState.currentPage,
                    pageCount = pagerState.pageCount
                )

                SideEffect {
                    readerVM.onPageChange(pagerState, context)
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingDialog(
    onDismissRequest: () -> Unit = {},
    pageCount: Int = 1,
    onSetView: (view: Int) -> Unit = {},
    onSetPage: (page: Int) -> Unit = {},
    onSetFontSize: (fontSize: TextUnit) -> Unit = {},
    onSetLineHeight: (lineHeight: TextUnit) -> Unit = {},
    onSetPadding: (padding: Dp) -> Unit = {},
    onJumpPage: () -> Unit = {}
) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier,
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(modifier = Modifier.padding(0.dp, 5.dp), fontSize = 24.sp, text = "选项")
                Text(modifier = Modifier.padding(0.dp, 5.dp), text = "Tips: 修改样式后建议重新进入")
                NumInputWithConfirm(
                    label = "跳转到网页的页数",
                    range = IntRange(1, 65535),
                    onConfirm = onSetView
                )
                NumInputWithConfirm(
                    label = "跳转到阅读器的页数",
                    range = IntRange(1, pageCount),
                    onConfirm = onSetPage
                )
                NumInputWithConfirm(
                    label = "字体大小 (范围10 - 50)",
                    range = IntRange(10, 50),
                    onConfirm = { onSetFontSize(it.sp) }
                )
                NumInputWithConfirm(
                    label = "行高 (范围10 - 200)",
                    range = IntRange(10, 100),
                    onConfirm = { onSetLineHeight(it.sp) }
                )
                NumInputWithConfirm(
                    label = "页边距",
                    range = IntRange(10, 100),
                    onConfirm = { onSetPadding(it.dp) }
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 10.dp, 0.dp, 0.dp), onClick = onJumpPage
                ) {
                    Text("跳转至上次阅读")
                }
            }

        }
    }
}

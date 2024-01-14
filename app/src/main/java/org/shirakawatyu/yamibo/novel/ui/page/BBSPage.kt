package org.shirakawatyu.yamibo.novel.ui.page

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import org.shirakawatyu.yamibo.novel.ui.widget.ComposableWebView

@Preview
@Composable
fun BBSPage() {
    ComposableWebView(url = "https://bbs.yamibo.com/forum.php")
}
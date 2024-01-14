package org.shirakawatyu.yamibo.novel.ui.page

import androidx.compose.runtime.Composable
import org.shirakawatyu.yamibo.novel.ui.widget.ComposableWebView

@Composable
fun MinePage() {
    ComposableWebView(url = "https://bbs.yamibo.com/home.php?mod=space&do=profile&mycenter=1&mobile=2")
}
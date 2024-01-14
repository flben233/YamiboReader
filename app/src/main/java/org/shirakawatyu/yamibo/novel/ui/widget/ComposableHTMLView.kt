package org.shirakawatyu.yamibo.novel.ui.widget

import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun ComposableHTMLView(html: String) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(0xfcf4cf)
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            loadData(html, "text/html", "utf-8")
        }
    })
}
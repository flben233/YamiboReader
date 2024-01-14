package org.shirakawatyu.yamibo.novel.ui.widget

import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.shirakawatyu.yamibo.novel.global.GlobalData

@Composable
fun ComposableWebView(url: String) {
    AndroidView(factory = { context ->
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            webViewClient = GlobalData.webViewClient
            webChromeClient = GlobalData.webChromeClient
            loadUrl(url)
        }
    })
}


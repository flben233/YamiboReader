package org.shirakawatyu.yamibo.novel.module

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebView
import org.shirakawatyu.yamibo.novel.global.GlobalData

class PassageWebViewClient(val onFinished: (html: String, url: String?) -> Unit): YamiboWebViewClient() {

    private val logTag = "PassageWebViewClient"

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        if (url != null) {
            Log.i(logTag, url)
        }
        GlobalData.loading = true
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        view?.evaluateJavascript("document.getElementsByTagName('html')[0].innerHTML") {
            var innerHTML = it.replace("\\u003C", "<").replace("\\\"", "\"").replace("\\n", "\n")
            innerHTML = "<html>${innerHTML.substring(1, innerHTML.length - 1)}</html>"
            if (url != null && !url.contains("authorid")) {
                view.evaluateJavascript("document.getElementsByClassName('nav-more-item')[0].click()") {}
            } else {
                onFinished(innerHTML, url)
                GlobalData.loading = false
            }
        }

        super.onPageFinished(view, url)
    }
}
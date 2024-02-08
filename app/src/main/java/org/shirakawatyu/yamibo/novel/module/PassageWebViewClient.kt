package org.shirakawatyu.yamibo.novel.module

import android.graphics.Bitmap
import android.util.Log
import android.webkit.WebView
import org.shirakawatyu.yamibo.novel.global.GlobalData

class PassageWebViewClient(val onFinished: (html: String, url: String?) -> Unit): YamiboWebViewClient() {

    private val logTag = "PassageWebViewClient"
    private val saveImgJs = """
        function getBase64Image(img, canvas) {
            canvas.width = img.naturalWidth > 1080 ? 1080 : img.naturalWidth; //设置canvas的宽度和图片一致
            canvas.height = canvas.width * img.naturalHeight / img.naturalWidth; //设置canvas的高度和图片一致
            var ctx = canvas.getContext("2d"); //获取canvas的2d绘图上下文
            ctx.drawImage(img, 0, 0, canvas.width, canvas.height); //将图片绘制到canvas上
            var dataURL = canvas.toDataURL("image/jpeg", 0.5); //将canvas的内容转换为base64编码的字符串
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            return dataURL.replace("data:image/jpeg;base64,", ""); //返回或使用base64字符串
        }
        let nodes = document.getElementsByClassName("message");
        var canvas = document.createElement("canvas"); 
        for (let node of nodes) {
            for (let ele of node.getElementsByTagName("img")) {
                ele.src = getBase64Image(ele, canvas);
            }
        }
        document.getElementsByTagName('html')[0].innerHTML;
    """.trimMargin()

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        if (url != null) {
            Log.i(logTag, url)
        }
        GlobalData.loading = true
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        view?.evaluateJavascript("document.getElementsByTagName('html')[0].innerHTML") {
            if (url != null && !url.contains("authorid")) {
                view.evaluateJavascript("document.getElementsByClassName('nav-more-item')[0].click()") {}
            } else {
                view.evaluateJavascript(saveImgJs) {
                    var innerHTML = it.replace("\\u003C", "<").replace("\\\"", "\"").replace("\\n", "\n")
                    innerHTML = "<html>${innerHTML.substring(1, innerHTML.length - 1)}</html>"
                    onFinished(innerHTML, url)
                    GlobalData.loading = false
                }
            }
        }

        super.onPageFinished(view, url)
    }
}
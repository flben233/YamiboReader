package org.shirakawatyu.yamibo.novel.util

class HTMLUtil {
    companion object {
        fun toText(html: String): String {
            return html
                .replace("<br>", "\n")
                .replace(Regex("<.*?>"), "")
                .replace("&nbsp;", " ")
                .replace(Regex("\n+"), "\n")
                .trim()
        }
    }
}
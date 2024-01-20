package org.shirakawatyu.yamibo.novel.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

class TextUtil {
    companion object {
        fun pagingText(
            text: String,
            height: Dp,
            width: Dp,
            fontSize: TextUnit,
            letterSpacing: TextUnit,
            lineHeight: TextUnit
        ): List<String> {
            val textLines = text.split("\n")
            val perLineNum = (ValueUtil.dpToPx(width).toInt() /
                    (ValueUtil.spToPx(fontSize) + ValueUtil.spToPx(letterSpacing)).toInt())
            val maxLine = (ValueUtil.dpToPx(height) / ValueUtil.spToPx(lineHeight)).toInt()
            val result = ArrayList<String>()
            val resultLines = ArrayList<String>()
            for (line in textLines) {
                if (line.trim().isEmpty()) {
                    continue
                }
                resultLines.addAll(line.chunked(perLineNum))
            }
            resultLines.chunked(maxLine).forEach {
                result.add(it.joinToString("\n") + "\n")
            }
            return result
        }

    }
}
package org.shirakawatyu.yamibo.novel.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import kotlin.math.ceil

class TextUtil {
    companion object {
        fun pagingText(
            text: String,
            height: Dp,
            width: Dp,
            fontSize: TextUnit,
            lineHeight: TextUnit
        ): List<String> {
            val textLines = text.split("\n")
            val perLineNum = (ValueUtil.dpToPx(width) / ValueUtil.spToPx(fontSize)).toInt()
            var lineNum = 0
            val maxLine = (ValueUtil.dpToPx(height) / ValueUtil.spToPx(lineHeight)).toInt() - 1
            val resultList = ArrayList<String>()
            var textPart = ""
            for (line in textLines) {
                val needLine = calculateLine(line.length, perLineNum)
                if (lineNum + needLine > maxLine) {
                    val part1 = (maxLine - lineNum) * perLineNum
                    resultList.add(textPart + line.substring(0, part1) + "\n")
                    textPart = line.substring(part1) + "\n"
                    lineNum = calculateLine(line.length - part1, perLineNum)
                } else {
                    textPart += (line + "\n")
                    lineNum += needLine
                }
            }
            resultList.add(textPart)
            return resultList
        }

        private fun calculateLine(wordNum: Int, perLineNum: Int): Int {
            return ceil(wordNum / perLineNum.toFloat()).toInt()
        }
    }
}
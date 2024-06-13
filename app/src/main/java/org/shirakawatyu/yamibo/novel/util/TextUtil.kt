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
//                resultLines.addAll(line.chunked(perLineNum))
                resultLines.addAll(chunkLine(line, perLineNum))
            }
            resultLines.chunked(maxLine).forEach {
                result.add(it.joinToString("\n") + "\n")
            }
            return result
        }

        private fun getWidth(c: Char): Double {
            return if (c.code in 0x0020..0x007e) 0.6 else 1.0
        }

        private fun getLastWordIndex(s: String): Int {
            val punctuation = Regex("\\p{Punct}")
            for (i in s.length - 1 downTo  0) {
                if (!punctuation.matches(s[i].toString())) {
                    return i
                }
            }
            return -1
        }

        private fun chunkLine(line: String, perLineNum: Int): List<String> {
            var cnt = 0.0
            val chunks = ArrayList<String>()
            var newLine = ArrayList<Char>()
            for (c in line) {
                if (cnt > perLineNum - 1) {
                    chunks.add(String(newLine.toCharArray()))
                    newLine = ArrayList()
                    cnt = 0.0
                }
                if (newLine.size == 0 && c.toString().matches(Regex("\\p{Punct}"))) {
                    val s = chunks[chunks.size - 1]
                    val wordSeq = s.substring(getLastWordIndex(s))
                    newLine.addAll(wordSeq.toList())
                    wordSeq.forEach {
                        cnt += getWidth(it)
                    }
                    chunks[chunks.size - 1] = s.substring(0, s.length - wordSeq.length)
                }
                cnt += getWidth(c)
                newLine.add(c)
            }
            if (newLine.isNotEmpty()) {
                chunks.add(String(newLine.toCharArray()))
            }
            return chunks
        }
    }
}
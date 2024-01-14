package org.shirakawatyu.yamibo.novel.ui.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.shirakawatyu.yamibo.novel.bean.Content
import org.shirakawatyu.yamibo.novel.bean.ContentType

@Composable
fun ContentViewer(
    data: Content,
    padding: Dp = 16.dp,
    lineHeight: TextUnit = 39.sp,
    fontSize: TextUnit = 18.sp,
    currentPage: Int,
    pageCount: Int
) {
    Column {
        if (data.type == ContentType.IMG) {
            ComposableHTMLView(html = "<img src=\"${data.data}\" style=\"width: 100vw; height: 100vh; object-fit: contain;\"/>")
        } else if (data.type == ContentType.TEXT) {
            Text(
                modifier = Modifier
                    .padding(padding)
                    .weight(1f)
                    .fillMaxSize(),
                lineHeight = lineHeight,
                fontSize = fontSize,
                color = Color.Black,
                text = data.data
            )
        }
        Text(
            modifier = Modifier.fillMaxWidth().padding(horizontal = padding),
            textAlign = TextAlign.End,
            lineHeight = lineHeight,
            fontSize = 12.sp,
            color = Color.DarkGray,
            text = "${currentPage}/${pageCount}"
        )
    }
}
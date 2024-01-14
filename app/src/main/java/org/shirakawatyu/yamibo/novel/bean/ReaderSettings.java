package org.shirakawatyu.yamibo.novel.bean;

import androidx.compose.ui.unit.Dp;

public class ReaderSettings {
    private Float fontSizePx;
    private Float lineHeightPx;
    private Dp padding;

    public ReaderSettings(Float fontSizePx, Float lineHeightPx, Dp padding) {
        this.fontSizePx = fontSizePx;
        this.lineHeightPx = lineHeightPx;
        this.padding = padding;
    }

    public ReaderSettings() {
    }

    public Float getFontSizePx() {
        return fontSizePx;
    }

    public void setFontSizePx(Float fontSizePx) {
        this.fontSizePx = fontSizePx;
    }

    public Float getLineHeightPx() {
        return lineHeightPx;
    }

    public void setLineHeightPx(Float lineHeightPx) {
        this.lineHeightPx = lineHeightPx;
    }

    public Dp getPadding() {
        return padding;
    }

    public void setPadding(Dp padding) {
        this.padding = padding;
    }
}

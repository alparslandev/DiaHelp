package com.diahelp.ui

import android.content.Context
import android.graphics.Canvas
import android.support.v7.widget.AppCompatImageButton
import android.util.AttributeSet

class CustomIconButton : AppCompatImageButton {
    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setAlpha(if (enabled) 1 else 0.4f)
    }
}
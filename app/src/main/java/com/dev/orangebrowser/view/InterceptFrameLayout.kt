package com.dev.orangebrowser.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout

class InterceptFrameLayout:FrameLayout {
    constructor(context: Context):super(context)
    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet)
    private var mOnTouchListener: OnTouchListener? = null
    fun setListener(onTouchListener: OnTouchListener?) {
        mOnTouchListener = onTouchListener
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        mOnTouchListener?.onTouch(this, ev)
        return super.dispatchTouchEvent(ev)
    }
}
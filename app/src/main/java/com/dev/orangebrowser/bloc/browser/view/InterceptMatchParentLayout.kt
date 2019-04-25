package com.dev.orangebrowser.bloc.browser.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.dev.view.MatchParentLayout

class InterceptMatchParentLayout:MatchParentLayout {
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
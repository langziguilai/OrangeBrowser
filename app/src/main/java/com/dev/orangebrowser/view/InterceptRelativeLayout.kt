package com.dev.orangebrowser.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.children

class InterceptRelativeLayout:FrameLayout {
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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        this.children.first().layout(0,0,right-left,bottom-top)
    }
}
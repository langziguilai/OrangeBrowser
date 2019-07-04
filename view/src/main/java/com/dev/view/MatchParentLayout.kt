package com.dev.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.dev.util.Keep
import com.dev.util.KeepMemberIfNecessary
import com.dev.util.KeepNameIfNecessary

/*
* @doc
*  容器让子View完全匹配父View的真实大小，而不管OnMeasure的大小
* */
@Keep
open class MatchParentLayout:FrameLayout{
    constructor(context: Context):super(context)
    constructor(context: Context,attributeSet: AttributeSet?):super(context,attributeSet)
    constructor(context: Context,attrs: AttributeSet? = null,defStyleAttr: Int = 0):super(context,attrs,defStyleAttr)
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var index=0
        while (index<childCount){
            val parentHeight=bottom-top
            val parentWidth=right-left
            getChildAt(index).layout(0,0,parentWidth,parentHeight)
            index++
        }
    }
}
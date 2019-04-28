package com.dev.orangebrowser.view

import android.content.Context
import android.util.AttributeSet
import android.view.View

abstract class TriggerView:View,OnTriggerView{
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}
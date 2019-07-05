package com.dev.orangebrowser.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.dev.util.KeepMemberIfNecessary
import com.dev.util.KeepNameIfNecessary

abstract class TriggerView:View,OnTriggerView{
    @KeepMemberIfNecessary
    constructor(context: Context) : super(context)
    @KeepMemberIfNecessary
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    @KeepMemberIfNecessary
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
}
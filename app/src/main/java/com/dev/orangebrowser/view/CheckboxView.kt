package com.dev.orangebrowser.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.dev.orangebrowser.R

class CheckboxView:LinearLayout,OnTriggerView {
    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView(context, attrs, defStyleAttr)
    }

    lateinit var checkBox:CheckBox
    lateinit var title:TextView
    private fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val view= LayoutInflater.from(context).inflate(R.layout.item_setting_checkbox,this)
        checkBox=view.findViewById(R.id.checkbox)
        title=view.findViewById(R.id.title)
    }

    override fun initial(data: Any) {
        checkBox.isChecked=false
    }

    override fun onTrigger() {
        checkBox.isChecked=!checkBox.isChecked
    }

    override fun clear() {
        checkBox.isChecked=false
    }

    override fun asView(): View {
        return this
    }
}
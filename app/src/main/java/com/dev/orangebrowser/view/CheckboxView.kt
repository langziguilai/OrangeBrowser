package com.dev.orangebrowser.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import com.dev.orangebrowser.R
import com.dev.util.KeepMemberIfNecessary
import com.dev.util.KeepNameIfNecessary

@KeepNameIfNecessary
class CheckboxView:LinearLayout,OnTriggerView {
    @KeepMemberIfNecessary
    constructor(context: Context) : this(context,null)
    @KeepMemberIfNecessary
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    @KeepMemberIfNecessary
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView(context, attrs, defStyleAttr)
    }
    @KeepMemberIfNecessary
    lateinit var checkBox:CheckBox
    @KeepMemberIfNecessary
    lateinit var title:TextView
    private fun initView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val view= LayoutInflater.from(context).inflate(R.layout.item_setting_checkbox,this)
        checkBox=view.findViewById(R.id.checkbox)
        title=view.findViewById(R.id.title)
    }
    @KeepMemberIfNecessary
    override fun initial(data: Any) {
        checkBox.isChecked=false
    }
    @KeepMemberIfNecessary
    override fun onTrigger() {
        checkBox.isChecked=!checkBox.isChecked
    }
    @KeepMemberIfNecessary
    override fun clear() {
        checkBox.isChecked=false
    }
    @KeepMemberIfNecessary
    override fun asView(): View {
        return this
    }
}
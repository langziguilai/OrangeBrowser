package com.dev.orangebrowser.bloc.setting.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolder
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolderHelper

class CheckboxViewHolder(var parentView:View): BaseViewHolder(parentView){
    private val titleTextView: TextView =parentView.findViewById(R.id.title)
    private val checkBox: CheckBox =parentView.findViewById(R.id.checkbox)
    override fun bindData(data: Any) {
        (data as? CheckboxItem)?.apply {
            titleTextView.text=data.title
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                data.action.invoke(isChecked)
            }
            checkBox.isChecked=data.value
        }

    }
}

data class CheckboxItem(var title:String,var action:Action<Boolean>,val value:Boolean)

class CheckboxViewHolderHelper:
    BaseViewHolderHelper(){
    override fun isMatched(data: Any): Boolean {
        return data is CheckboxItem
    }

    override fun getType(): Int {
        return R.layout.item_setting_checkbox
    }

    override fun createViewHolder(parent:ViewGroup):CheckboxViewHolder{
       val itemView= LayoutInflater.from(parent.context).inflate(getType(),parent,false)
        return CheckboxViewHolder(itemView)
    }
}
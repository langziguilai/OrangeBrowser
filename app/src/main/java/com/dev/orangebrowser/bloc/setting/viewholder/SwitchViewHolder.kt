package com.dev.orangebrowser.bloc.setting.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolder
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolderHelper
import com.dev.view.IconfontTextView

class SwitchViewHolder(var parentView:View): BaseViewHolder(parentView){
    private val titleTextView: TextView =parentView.findViewById(R.id.title)
    private val switch: Switch =parentView.findViewById(R.id.switchToggle)
    override fun bindData(data: Any) {
        (data as? SwitchItem)?.apply {
            titleTextView.text=data.title
            switch.setOnCheckedChangeListener { _, isChecked ->
                data.action.invoke(isChecked)
            }
            switch.isChecked=data.value
        }

    }
}

data class SwitchItem(var title:String,var action:Action<Boolean>,val value:Boolean)

class SwitchViewHolderHelper:
    BaseViewHolderHelper(){
    override fun isMatched(data: Any): Boolean {
        return data is SwitchItem
    }

    override fun getType(): Int {
        return R.layout.item_setting_switch
    }

    override fun createViewHolder(parent:ViewGroup):SwitchViewHolder{
       val itemView= LayoutInflater.from(parent.context).inflate(getType(),parent,false)
        return SwitchViewHolder(itemView)
    }
}
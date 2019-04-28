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

class TickViewHolder(var parentView:View): BaseViewHolder(parentView){
    private val titleTextView: TextView =parentView.findViewById(R.id.title)
    private val tick: CheckBox =parentView.findViewById(R.id.tick)
    override fun bindData(data: Any) {
        (data as? TickItem)?.apply {
            titleTextView.text=data.title
            tick.isChecked=data.value
            parentView.setOnClickListener {
                data.action.invoke(data)
            }
        }

    }
}

data class TickItem(var title:String,var action:Action<TickItem>,var value:Boolean)

class TickViewHolderHelper:
    BaseViewHolderHelper(){
    override fun isMatched(data: Any): Boolean {
        return data is TickItem
    }

    override fun getType(): Int {
        return R.layout.item_setting_tick
    }

    override fun createViewHolder(parent:ViewGroup):TickViewHolder{
       val itemView= LayoutInflater.from(parent.context).inflate(getType(),parent,false)
        return TickViewHolder(itemView)
    }
}
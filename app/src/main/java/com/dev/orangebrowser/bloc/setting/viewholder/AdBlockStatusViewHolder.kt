package com.dev.orangebrowser.bloc.setting.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolder
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolderHelper
import com.dev.view.IconfontTextView

class AdBlockStatusViewHolder(var parentView:View): BaseViewHolder(parentView){
    private val adBlockCountTextView: TextView =parentView.findViewById(R.id.ad_block_count)
    private val statusTextView: TextView =parentView.findViewById(R.id.status)

    override fun bindData(data: Any) {
        (data as? AdblockStatusItem)?.apply {
               adBlockCountTextView.text=data.count
               statusTextView.text=data.status
        }

    }
}

data class AdblockStatusItem(var count:String,var status:String)

class AdblockStatusViewHolderHelper:
    BaseViewHolderHelper(){
    override fun isMatched(data: Any): Boolean {
        return data is AdblockStatusItem
    }

    override fun getType(): Int {
        return R.layout.item_setting_adblock_status
    }

    override fun createViewHolder(parent:ViewGroup):AdBlockStatusViewHolder{
       val itemView= LayoutInflater.from(parent.context).inflate(getType(),parent,false)
        return AdBlockStatusViewHolder(itemView)
    }
}
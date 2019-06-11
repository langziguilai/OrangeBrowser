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

class FolderViewHolder(var parentView:View): BaseViewHolder(parentView){
    private val titleTextView: TextView =parentView.findViewById(R.id.title)
    private val iconTextView: TextView =parentView.findViewById(R.id.primary_icon)
    private val iconView: IconfontTextView =parentView.findViewById(R.id.icon)

    override fun bindData(data: Any) {
        (data as? FolderItem)?.apply {
            iconView.text=data.icon
            titleTextView.text=data.title
            iconTextView.text=data.iconText
            iconTextView.setTextColor(color)
            parentView.setOnClickListener {
                data.action.invoke(data)
            }
        }

    }
}

data class FolderItem(var iconText:String,var color:Int,var title:String,var path:String,var icon:String,var action:Action<FolderItem>)

class FolderViewHolderHelper:
    BaseViewHolderHelper(){
    override fun isMatched(data: Any): Boolean {
        return data is FolderItem
    }

    override fun getType(): Int {
        return R.layout.item_setting_icon_tile
    }

    override fun createViewHolder(parent:ViewGroup):FolderViewHolder{
       val itemView= LayoutInflater.from(parent.context).inflate(getType(),parent,false)
        return FolderViewHolder(itemView)
    }
}
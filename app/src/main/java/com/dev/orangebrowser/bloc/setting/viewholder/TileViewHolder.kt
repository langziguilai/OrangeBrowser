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

class TileViewHolder(var parentView:View): BaseViewHolder(parentView){
    private val titleTextView: TextView =parentView.findViewById(R.id.title)
    private val tipTextView: TextView =parentView.findViewById(R.id.tip)
    private val iconView: IconfontTextView =parentView.findViewById(R.id.icon)

    override fun bindData(data: Any) {
        (data as? TileItem)?.apply {
            iconView.text=data.icon
            tipTextView.text=data.tip
            titleTextView.text=data.title
            parentView.setOnClickListener {
                data.action.invoke(data)
            }
        }

    }
}

data class TileItem(var title:String,var tip:String,var icon:String,var action:Action<TileItem>)

class TileViewHolderHelper:
    BaseViewHolderHelper(){
    override fun isMatched(data: Any): Boolean {
        return data is TileItem
    }

    override fun getType(): Int {
        return R.layout.item_setting_tile
    }

    override fun createViewHolder(parent:ViewGroup):TileViewHolder{
       val itemView= LayoutInflater.from(parent.context).inflate(getType(),parent,false)
        return TileViewHolder(itemView)
    }
}
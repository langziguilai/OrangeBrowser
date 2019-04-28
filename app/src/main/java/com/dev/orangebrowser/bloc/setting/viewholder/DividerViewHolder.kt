package com.dev.orangebrowser.bloc.setting.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolder
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolderHelper
import com.dev.util.DensityUtil

class DividerViewHolder(var item:View): BaseViewHolder(item){
    override fun bindData(data: Any) {
        (data as? DividerItem)?.apply {
            val height=DensityUtil.dip2px(item.context,data.height.toFloat())
            val params=item.layoutParams
            params.height=height
            item.layoutParams=params
            item.setBackgroundColor(data.background)
        }
    }
}

data class DividerItem(var height:Int,var background:Int=0xFFFFFF)

class DividerViewHolderHelper:BaseViewHolderHelper(){
    override fun isMatched(data: Any): Boolean {
        return data is DividerItem
    }

    override fun getType(): Int {
        return R.layout.item_setting_divider
    }

    override fun createViewHolder(parent:ViewGroup): DividerViewHolder {
       val itemView= LayoutInflater.from(parent.context).inflate(getType(),parent,false)
        return DividerViewHolder(itemView)
    }
}
package com.dev.orangebrowser.bloc.setting.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolder
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolderHelper
import com.dev.util.DensityUtil
import com.dev.view.IconfontTextView

class CategoryHeaderViewHolder(var parentView:View): BaseViewHolder(parentView){
    private val titleTextView: TextView =parentView.findViewById(R.id.category_title)

    override fun bindData(data: Any) {
        (data as? CategoryHeaderItem)?.apply {
            titleTextView.text=data.title
            val params=titleTextView.layoutParams
            params.height=DensityUtil.dip2px(itemView.context,height.toFloat())
            titleTextView.layoutParams=params
            titleTextView.setBackgroundColor(data.background)
        }
    }
}

data class CategoryHeaderItem(var title:String,val height:Int,val background:Int)

class CategoryHeaderViewHolderHelper:
    BaseViewHolderHelper(){
    override fun isMatched(data: Any): Boolean {
        return data is CategoryHeaderItem
    }

    override fun getType(): Int {
        return R.layout.item_setting_category_header
    }

    override fun createViewHolder(parent:ViewGroup):CategoryHeaderViewHolder{
       val itemView= LayoutInflater.from(parent.context).inflate(getType(),parent,false)
        return CategoryHeaderViewHolder(itemView)
    }
}
package com.dev.orangebrowser.bloc.setting.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dev.orangebrowser.R
import com.dev.orangebrowser.bloc.setting.viewholder.base.Action
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolder
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolderHelper
import com.dev.view.IconfontTextView

class PathViewHolder(var parentView:View): BaseViewHolder(parentView){
    private val titleTextView: TextView =parentView.findViewById(R.id.title)

    override fun bindData(data: Any) {
        (data as? PathItem)?.apply {
//            if (data.path=="/"){
//                val layoutParams=parentView.layoutParams as RecyclerView.LayoutParams
//                layoutParams.marginEnd=0
//                parentView.layoutParams=layoutParams
//            }
            titleTextView.text=data.title
            parentView.background=parentView.context.getDrawable(data.bgResId)
            parentView.setOnClickListener {
                data.action.invoke(data)
            }
        }

    }
}

data class PathItem(var title:String,var path:String,var bgResId:Int,var action:Action<PathItem>)

class PathViewHolderHelper:
    BaseViewHolderHelper(){
    override fun isMatched(data: Any): Boolean {
        return data is PathItem
    }

    override fun getType(): Int {
        return R.layout.item_setting_path
    }

    override fun createViewHolder(parent:ViewGroup):PathViewHolder{
       val itemView= LayoutInflater.from(parent.context).inflate(getType(),parent,false)
        return PathViewHolder(itemView)
    }
}
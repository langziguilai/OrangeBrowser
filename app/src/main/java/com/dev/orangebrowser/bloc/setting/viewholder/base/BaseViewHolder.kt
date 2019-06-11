package com.dev.orangebrowser.bloc.setting.viewholder.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView){
    abstract fun bindData(data:Any)
}
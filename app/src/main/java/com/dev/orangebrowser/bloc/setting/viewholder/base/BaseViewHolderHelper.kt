package com.dev.orangebrowser.bloc.setting.viewholder.base

import android.view.ViewGroup

abstract class BaseViewHolderHelper{
    abstract fun getType():Int
    abstract fun createViewHolder(parent:ViewGroup): BaseViewHolder
    abstract fun isMatched(data:Any):Boolean
}
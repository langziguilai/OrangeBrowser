package com.dev.orangebrowser.bloc.setting.viewholder.base

interface Action<T:Any>{
    fun invoke(data:T)
}
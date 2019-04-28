package com.dev.orangebrowser.bloc.setting.adapter

import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.recyclerview.widget.RecyclerView
import com.dev.orangebrowser.bloc.setting.viewholder.*
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolder
import com.dev.orangebrowser.bloc.setting.viewholder.base.BaseViewHolderHelper
import java.lang.Exception

class Adapter<T:Any>(private var dataList: List<T>) : RecyclerView.Adapter<BaseViewHolder>() {
    private val helperMap=SparseArray<BaseViewHolderHelper>()

    init {
        addHelper(DividerViewHolderHelper())
        addHelper(SwitchViewHolderHelper())
        addHelper(CategoryHeaderViewHolderHelper())
        addHelper(TileViewHolderHelper())
        addHelper(CheckboxViewHolderHelper())
        addHelper(TickViewHolderHelper())
    }


    fun addHelper(helper: BaseViewHolderHelper){
        helperMap.put(helper.getType(),helper)
    }
    override fun getItemViewType(position: Int): Int {
        helperMap.forEach { _, helper ->
           if(helper.isMatched(dataList[position])){
               return helper.getType()
           }
        }
        throw Exception("can't find view holder for this kind of dataList")
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):BaseViewHolder {
       return  helperMap.get(viewType).createViewHolder(parent)
    }

    override fun getItemCount(): Int {
       return dataList.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
         holder.bindData(dataList[position])
    }
}
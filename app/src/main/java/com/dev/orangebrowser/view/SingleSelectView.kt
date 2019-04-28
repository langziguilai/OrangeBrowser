package com.dev.orangebrowser.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.core.view.children

abstract class SingleSelectView<T:Any> : LinearLayout {
    interface OnItemSelectListener<T> {
        fun onItemSelect(data: T)
    }
    private lateinit var dataList: List<T>
    var listener: OnItemSelectListener<T>? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    //添加数据
    fun setOptions(options: List<T>) {
        dataList = options
        dataList.forEach {
            val data = it
            val onTriggerView = generateChildView(it)
            onTriggerView.initial(it)
            val view=onTriggerView.asView()
            view.isClickable = true
            view.isFocusable = true
            view.setOnClickListener {
                onSelect(data)
            }
            addView(view)
        }
    }

    //设置监听器
    fun setOnItemSelectListener(listener: OnItemSelectListener<T>) {
        this.listener = listener
    }

    private fun onSelect(data: T) {
        //先清空
        children.forEach {
            (it as? TriggerView)?.clear()
        }
        //再选中
        val index = dataList.indexOf(data)
        if (index >= 0) {
            (children.elementAt(index) as? OnTriggerView)?.onTrigger()
            //再触发东西
            listener?.onItemSelect(data)
        }
    }
    abstract fun generateChildView(data: T): OnTriggerView
}
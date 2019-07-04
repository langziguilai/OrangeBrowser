package com.dev.base

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.dev.util.Keep
import com.google.android.material.bottomsheet.BottomSheetDialog

@Keep
abstract class BaseBottomSheetDialogFragment:CoroutineScopeBottomSheetDialogFragment(){
    abstract fun getLayoutResId():Int
    //自定义背景颜色,默认为透明色
    open fun setCustomBackgroundColor(view:View){
        (view.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
    }
    //在view被创建好之后初始化view
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        val view = View.inflate(context,getLayoutResId(), null)
        dialog.setContentView(view)
        setCustomBackgroundColor(view)
        initView(view,savedInstanceState)
        return dialog
    }
    abstract fun initView(view:View, savedInstanceState: Bundle?)
    //在activity被创建好之后初始化数据
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initData(savedInstanceState)
    }

    abstract fun initData(savedInstanceState: Bundle?)
}
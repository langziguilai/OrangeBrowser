package com.dev.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView


class AlertDialogBuilder{
    private var layoutId:Int=0
    private var onNegative:Runnable?=null
    private var onPositive:Runnable?=null
    private var title:String=""
    private var titleId:Int=-1
    private var content:String=""
    private var contentId:Int=-1
    private var positiveButtonText:String="确定"
    private var positiveButtonTextId:Int=-1
    private var negativeButtonText:String="取消"
    private var negativeButtonTextId:Int=-1
    fun setLayoutId(layoutId:Int):AlertDialogBuilder{
        this.layoutId=layoutId
        return this
    }
    fun setonNegative(onNegative:Runnable):AlertDialogBuilder{
        this.onNegative=onNegative
        return this
    }
    fun setOnPositive(onPositive:Runnable):AlertDialogBuilder{
        this.onPositive=onPositive
        return this
    }
    fun setTitle(id:Int,title:String):AlertDialogBuilder{
        this.titleId=id
        this.title=title
        return this
    }
    fun setContent(id:Int,content:String):AlertDialogBuilder{
        this.contentId=id
        this.content=content
        return this
    }
    fun setPositiveButtonText(id:Int,text:String):AlertDialogBuilder{
        this.positiveButtonTextId=id
        this.positiveButtonText=text
        return this
    }
    fun setNegativeButtonText(id:Int,text:String):AlertDialogBuilder{
        this.negativeButtonTextId=id
        this.negativeButtonText=text
        return this
    }
    fun build(context:Context): Dialog {
        return DialogBuilder()
            .setLayoutId(layoutId)
            .setHeightParent(1f)
            .setWidthPercent(1f)
            .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                override fun onViewCreated(view: View) {
                    view.findViewById<TextView>(titleId).text=title
                    view.findViewById<TextView>(contentId).text=content
                    view.findViewById<TextView>(positiveButtonTextId).text=positiveButtonText
                    view.findViewById<TextView>(negativeButtonTextId).text=negativeButtonText
                }
            })
            .setGravity(Gravity.CENTER)
            .build(context)
    }
}
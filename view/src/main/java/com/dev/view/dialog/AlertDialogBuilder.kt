package com.dev.view.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.dev.view.R


class AlertDialogBuilder{
    private var layoutId:Int=R.layout.dialog_alert
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
    private var enterAnimationId: Int = R.anim.slide_up_alert
    private var exitAnimationId:Int=  R.anim.slide_down_alert
    private var gravity:Int=Gravity.BOTTOM
    private var dialog:Dialog?=null
    fun setEnterAnimation(animationId: Int): AlertDialogBuilder {
        this.enterAnimationId = animationId
        return this
    }
    fun setExitAnimationId(animationId: Int): AlertDialogBuilder {
        this.exitAnimationId = animationId
        return this
    }
    fun setGravity(gravity:Int):AlertDialogBuilder{
        this.gravity=gravity
        return this
    }
    fun setLayoutId(layoutId:Int=R.layout.dialog_alert):AlertDialogBuilder{
        this.layoutId=layoutId
        return this
    }
    fun setOnNegative(onNegative:Runnable):AlertDialogBuilder{
        this.onNegative=onNegative
        return this
    }
    fun setOnPositive(onPositive:Runnable):AlertDialogBuilder{
        this.onPositive=onPositive
        return this
    }
    fun setTitle(id:Int=R.id.title,title:String):AlertDialogBuilder{
        this.titleId=id
        this.title=title
        return this
    }
    fun setContent(id:Int= R.id.content,content:String):AlertDialogBuilder{
        this.contentId=id
        this.content=content
        return this
    }
    fun setPositiveButtonText(id:Int= R.id.sure, text:String):AlertDialogBuilder{
        this.positiveButtonTextId=id
        this.positiveButtonText=text
        return this
    }
    fun setNegativeButtonText(id:Int= R.id.cancel,text:String):AlertDialogBuilder{
        this.negativeButtonTextId=id
        this.negativeButtonText=text
        return this
    }
    fun build(context:Context): Dialog {
        if (dialog==null){
            dialog= DialogBuilder()
                .setLayoutId(layoutId)
                .setGravity(gravity)
                .setExitAnimationId(exitAnimationId)
                .setEnterAnimationId(enterAnimationId)
                .setWidthPercent(1f)
                .setCanceledOnTouchOutside(true)
                .setOnViewCreateListener(object : DialogBuilder.OnViewCreateListener {
                    override fun onViewCreated(view: View) {
                        view.findViewById<TextView>(titleId)?.apply {
                            text=title
                        }
                        view.findViewById<TextView>(contentId)?.apply {
                            text=content
                        }
                        view.findViewById<TextView>(positiveButtonTextId)?.apply {
                            isClickable=true
                            isFocusable=true
                            text=positiveButtonText
                            setOnClickListener {
                                onPositive?.run()
                                if (dialog!=null && dialog!!.isShowing){
                                    dialog?.dismiss()
                                }
                            }
                        }
                        view.findViewById<TextView>(negativeButtonTextId)?.apply {
                            isClickable=true
                            isFocusable=true
                            text=negativeButtonText
                            setOnClickListener {
                                onNegative?.run()
                                if (dialog!=null && dialog!!.isShowing){
                                    dialog?.dismiss()
                                }
                            }
                        }
                    }
                })
                .build(context)
        }
         return dialog!!
    }
}
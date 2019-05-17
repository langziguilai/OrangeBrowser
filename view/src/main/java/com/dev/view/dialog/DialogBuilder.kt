package com.dev.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDialog
import com.dev.view.R

open class DialogBuilder {
    interface OnViewCreateListener {
        fun onViewCreated(view: View)
    }

    lateinit var view: View
    private var widthPercent: Float = 1f
    private var heightPercent: Float = -1f
    private var layoutId: Int = -1
    private var exitAnimationId: Int = -1
    private var cancel: Boolean = true
    private var gravity: Int = Gravity.CENTER
    private var listener: OnViewCreateListener? = null
    private var enterAnimationId: Int = -1
    private var backgroundColorId:Int=-1
    private var locationX: Int = 0
    private var locationY: Int = 0

    fun setBackgourndColorId(backgroundColorId:Int):DialogBuilder{
        this.backgroundColorId=backgroundColorId
        return this
    }
    fun setWidthPercent(wp: Float): DialogBuilder {
        this.widthPercent = wp
        return this
    }

    fun setHeightParent(hp: Float): DialogBuilder {
        this.heightPercent = hp
        return this
    }

    fun setLayoutId(layoutId: Int): DialogBuilder {
        this.layoutId = layoutId
        return this
    }

    fun setExitAnimationId(animationId: Int): DialogBuilder {
        this.exitAnimationId = animationId
        return this
    }

    fun setCanceledOnTouchOutside(cancel: Boolean): DialogBuilder {
        this.cancel = cancel
        return this
    }

    fun setGravity(gravity: Int): DialogBuilder {
        this.gravity = gravity
        return this
    }

    fun setEnterAnimation(animationId: Int): DialogBuilder {
        this.enterAnimationId = animationId
        return this
    }

    fun setOnViewCreateListener(listener: OnViewCreateListener): DialogBuilder {
        this.listener = listener
        return this
    }
    fun setLocationX(x: Int): DialogBuilder {
        this.locationX = x
        return this
    }

    fun setLocationY(y: Int): DialogBuilder {
        this.locationY = y
        return this
    }

    fun build(context: Context): Dialog {
        val dialog = MyDialog(context, R.style.Dialog)
        dialog.setCanceledOnTouchOutside(this.cancel)
        if (layoutId <= 0) throw Exception("please set layout Id")
        val container= FrameLayout(context)
        dialog.setContentView(container)
        dialog.setExitAnimationId(this.exitAnimationId)
        dialog.setEnterAnimationId(this.enterAnimationId)
        dialog.window?.setGravity(this.gravity)
        val containerLayoutParams=container.layoutParams
        containerLayoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT
        containerLayoutParams.height=ViewGroup.LayoutParams.MATCH_PARENT
        container.layoutParams=containerLayoutParams
        view = LayoutInflater.from(context).inflate(this.layoutId, null)
        container.addView(view)
        container.isClickable=true
        container.isFocusable=true
        container.setOnClickListener {
            dialog.dismiss()
        }
        if (locationX != 0) {
            val params = container.layoutParams as FrameLayout.LayoutParams
            params.marginStart = locationX
            container.layoutParams = params
            dialog.window?.setGravity(Gravity.TOP)
        }
        if (locationY != 0) {
            val params = container.layoutParams as FrameLayout.LayoutParams
            params.topMargin = locationY
            container.layoutParams = params
            dialog.window?.setGravity(Gravity.TOP)
        }
        val layoutParams = view.layoutParams as FrameLayout.LayoutParams

        if (widthPercent > 0) {
            layoutParams.width = (view.context.resources.displayMetrics.widthPixels * widthPercent).toInt()
        }else{
            layoutParams.width= ViewGroup.LayoutParams.MATCH_PARENT
        }
        if (heightPercent > 0) {
            layoutParams.height = (view.context.resources.displayMetrics.heightPixels * heightPercent).toInt()
        }else{
            layoutParams.height= ViewGroup.LayoutParams.WRAP_CONTENT
        }
        if (backgroundColorId>0){
            container.setBackgroundColor(context.resources.getColor(backgroundColorId))
            dialog.window?.setBackgroundDrawable(ColorDrawable(context.resources.getColor(backgroundColorId)))
        }
        view.layoutParams = layoutParams
        listener?.onViewCreated(view)
        return dialog
    }
}

open class MyDialog : AppCompatDialog {
    constructor(context: Context) : super(context)
    constructor(context: Context, themeId: Int) : super(context, themeId)

    private var enterAnimationId = -1
    private var exitAnimationId = -1
    private var view: View? = null
    override fun setContentView(view: View) {
        this.view = view
        super.setContentView(view)
    }

    fun setEnterAnimationId(enterAnimationId: Int) {
        this.enterAnimationId = enterAnimationId
    }

    fun setExitAnimationId(exitAnimationId: Int) {
        this.exitAnimationId = exitAnimationId
    }

    override fun show() {
        super.show()
        if (enterAnimationId > 0 && view != null) {
            val animation = AnimationUtils.loadAnimation(view!!.context, enterAnimationId)
            view!!.startAnimation(animation)
        }
    }
    override fun dismiss() {
        if (exitAnimationId > 0 && view != null) {
            val animation = AnimationUtils.loadAnimation(view!!.context, exitAnimationId)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    super@MyDialog.dismiss()
                }
            })
            view!!.startAnimation(animation)
        } else {
            super.dismiss()
        }
    }
}
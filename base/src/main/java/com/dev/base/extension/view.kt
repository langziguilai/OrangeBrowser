package com.dev.base.extension

import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator

const val FAST_ANIMATION=100L
const val NORMAL_ANIMATION=500L
const val SLOW_ANIMATION=10000L
//从下出现的动画
//从下向上出现
fun View.slideUpIn(){
    this.animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).start()
}
//从上向下消失
fun View.slideUpOut(){
    this.animate().translationY(this.height.toFloat()).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).start()
}

//从上出现的动画
//从上向下出现
fun View.slideDownIn(){
    this.animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).start()
}
//从下向上消失
fun View.slideDownOut(){
    this.animate().translationY(-this.height.toFloat()).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).start()
}
//从右出现的动画
fun View.slideLeftIn(){
    this.animate().translationX(0f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).start()
}
fun View.slideLeftOut(){
    this.animate().translationX(this.width.toFloat()).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).start()
}
//从左出现的动画
fun View.slideRightIn(){
    this.animate().translationY(0f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).start()
}
fun View.slideRightOut(){
    this.animate().translationY(-this.width.toFloat()).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).start()
}

//不透明度动画
fun View.fadeIn(){
    this.animate().alpha(1f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).start()
}
fun View.fadeOut(){
    this.animate().alpha(0f).setDuration(FAST_ANIMATION).setInterpolator(AccelerateInterpolator()).start()
}

fun View.isShowing():Boolean{
    return this.visibility==View.VISIBLE
}
fun View.isHidden():Boolean{
    return this.visibility==View.GONE
}
//初始化View位置时：位于FrameLayout顶部的View隐藏到顶部以上
fun View.initialHideOnTop(){
    this.onGlobalLayoutComplete{
        it.animate().translationY(-this.height.toFloat()).setDuration(0).start()
    }
}
//初始化View位置时：位于FrameLayout底部的View隐藏到底部以下
fun View.initialHideOnBottom(){
    this.onGlobalLayoutComplete{
        it.animate().translationY(this.height.toFloat()).setDuration(0).start()
    }
}
//初始化View位置时：位于FrameLayout左边的View隐藏到左边以上
fun View.initialHideOnLeft(){
    this.onGlobalLayoutComplete {
        it.animate().translationX(-this.width.toFloat()).setDuration(0).start()
    }
}
//初始化View位置时：位于FrameLayout右边的View隐藏到右边以上
fun View.initialHideOnRight(){
    this.onGlobalLayoutComplete {
        it.animate().translationX(this.width.toFloat()).setDuration(0).start()
    }
}
fun View.hide(){
    this.visibility=View.GONE
}

fun View.show(){
    this.visibility=View.VISIBLE
}

//在layout之后调用：可以获取View的Height,Width等等属性
fun View.onGlobalLayoutComplete(callback:(View)->Unit){
    var listener:ViewTreeObserver.OnGlobalLayoutListener?=null
    listener= ViewTreeObserver.OnGlobalLayoutListener {
        callback(this)
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
    this.viewTreeObserver.addOnGlobalLayoutListener(listener)
}
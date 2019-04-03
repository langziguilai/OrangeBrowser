package com.dev.base.extension

import android.view.View
import android.view.animation.OvershootInterpolator

const val FAST_ANIMATION=300L
const val NORMAL_ANIMATION=500L
const val SLOW_ANIMATION=1000L
//从下出现的动画
//从下向上出现
fun View.slideUpIn(){
    this.animate().translationY(-1f).setDuration(FAST_ANIMATION).setInterpolator(OvershootInterpolator()).start()
}
//从上向下消失
fun View.slideUpOut(){
    this.animate().translationY(1f).setDuration(FAST_ANIMATION).setInterpolator(OvershootInterpolator()).start()
}

//从上出现的动画
//从上向下出现
fun View.slideDownIn(){
    this.animate().translationY(1f).setDuration(FAST_ANIMATION).setInterpolator(OvershootInterpolator()).start()
}
//从下向上消失
fun View.slideDownOut(){
    this.animate().translationY(-1f).setDuration(FAST_ANIMATION).setInterpolator(OvershootInterpolator()).start()
}
//从右出现的动画
fun View.slideLeftIn(){
    this.animate().translationX(-1f).setDuration(FAST_ANIMATION).setInterpolator(OvershootInterpolator()).start()
}
fun View.slideLeftOut(){
    this.animate().translationX(1f).setDuration(FAST_ANIMATION).setInterpolator(OvershootInterpolator()).start()
}
//从左出现的动画
fun View.slideRightIn(){
    this.animate().translationY(1f).setDuration(FAST_ANIMATION).setInterpolator(OvershootInterpolator()).start()
}
fun View.slideRightOut(){
    this.animate().translationY(-1f).setDuration(FAST_ANIMATION).setInterpolator(OvershootInterpolator()).start()
}

//不透明度动画
fun View.fadeIn(){
    this.animate().alpha(1f).setDuration(FAST_ANIMATION).setInterpolator(OvershootInterpolator()).start()
}
fun View.fadeOut(){
    this.animate().alpha(0f).setDuration(FAST_ANIMATION).setInterpolator(OvershootInterpolator()).start()
}
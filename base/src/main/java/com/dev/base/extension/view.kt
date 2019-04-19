package com.dev.base.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.view.inputmethod.InputMethodManager
import com.dev.util.CommonUtil
import java.lang.ref.WeakReference

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
/**
 * Performs the given action on each View in this ViewGroup.
 */
fun ViewGroup.forEach(action: (View) -> Unit) {
    for (index in 0 until childCount) {
        action(getChildAt(index))
    }
}
//截图
fun View.capture(config:Bitmap.Config= Bitmap.Config.RGB_565):Bitmap?{
    val v=this
    if (v.width<=0 || v.height<=0)
        return null
    val density=v.context.resources.displayMetrics.density
    val fullSizeBitmap = Bitmap.createBitmap(v.width, v.height,config)
    val c = Canvas(fullSizeBitmap)
    v.layout(v.left, v.top, v.right, v.bottom)
    v.draw(c)
    val sampleBitmap = CommonUtil.getResizedBitmap(fullSizeBitmap,v.height/density,v.width/density)
    fullSizeBitmap?.recycle()
    return sampleBitmap
}
/**
 * Tries to focus this view and show the soft input window for it.
 *
 *  @param flags Provides additional operating flags to be used with InputMethodManager.showSoftInput().
 *  Currently may be 0, SHOW_IMPLICIT or SHOW_FORCED.
 */
fun View.showKeyboard(flags: Int = InputMethodManager.SHOW_IMPLICIT) {
    ShowKeyboard(this, flags).post()
}

/**
 * Hides the soft input window.
 */
fun View.hideKeyboard() {
    val imm = (context.getSystemService(Context.INPUT_METHOD_SERVICE) ?: return)
            as InputMethodManager

    imm.hideSoftInputFromWindow(windowToken, 0)
}

private class ShowKeyboard(
    view: View,
    private val flags: Int = InputMethodManager.SHOW_IMPLICIT
) : Runnable {
    private val weakReference: WeakReference<View> = WeakReference(view)
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var tries: Int = TRIES

    override fun run() {
        weakReference.get()?.let { view ->
            if (!view.isFocusable || !view.isFocusableInTouchMode) {
                // The view is not focusable - we can't show the keyboard for it.
                return
            }

            if (!view.requestFocus()) {
                // Focus this view first.
                post()
                return
            }

            view.context?.systemService<InputMethodManager>(Context.INPUT_METHOD_SERVICE)?.let { imm ->
                if (!imm.isActive(view)) {
                    // This view is not the currently active view for the input method yet.
                    post()
                    return
                }

                if (!imm.showSoftInput(view, flags)) {
                    // Showing they keyboard failed. Try again later.
                    post()
                }
            }
        }
    }

    fun post() {
        tries--

        if (tries > 0) {
            handler.postDelayed(this, INTERVAL_MS)
        }
    }

    companion object {
        private const val INTERVAL_MS = 100L
        private const val TRIES = 10
    }
}
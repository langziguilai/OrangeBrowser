package com.dev.base.extension

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.core.animation.doOnEnd
import com.dev.util.CommonUtil
import java.lang.ref.WeakReference
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import com.dev.util.Keep


const val FAST_ANIMATION = 200L
const val NORMAL_ANIMATION = 300L
const val SLOW_ANIMATION = 10000L
val DEFAULT_INTERPOLATOR=AccelerateDecelerateInterpolator()
@Keep
fun View.isShowing(): Boolean {
    return this.visibility == View.VISIBLE
}
@Keep
fun View.isHidden(): Boolean {
    return this.visibility == View.GONE
}
@Keep
fun View.hide() {
    this.visibility = View.GONE
}
@Keep
fun View.show() {
    this.visibility = View.VISIBLE
}
@Keep
fun View.getBitmap():Bitmap{
    val b = Bitmap.createBitmap(
        this.width,
        this.height,
        Bitmap.Config.ARGB_8888
    )
    val c = Canvas(b)
    this.draw(c)
    return b
}
//在layout之后调用：可以获取View的Height,Width等等属性
@Keep
fun View.onGlobalLayoutComplete(callback: (View) -> Unit) {
    var listener: ViewTreeObserver.OnGlobalLayoutListener? = null
    listener = ViewTreeObserver.OnGlobalLayoutListener {
        callback(this)
        viewTreeObserver.removeOnGlobalLayoutListener(listener)
    }
    this.viewTreeObserver.addOnGlobalLayoutListener(listener)
}

/**
 * Performs the given action on each View in this ViewGroup.
 */
@Keep
fun ViewGroup.forEach(action: (View) -> Unit) {
    for (index in 0 until childCount) {
        action(getChildAt(index))
    }
}

//截图
@Keep
fun View.capture(config: Bitmap.Config = Bitmap.Config.ARGB_8888): Bitmap? {
    val v = this
    if (v.width <= 0 || v.height <= 0)
        return null
    val density = v.context.resources.displayMetrics.density
    val fullSizeBitmap = Bitmap.createBitmap(v.width, v.height, config)
    val c = Canvas(fullSizeBitmap)
    v.layout(v.left, v.top, v.right, v.bottom)
    v.draw(c)
    //val sampleBitmap = CommonUtil.getResizedBitmap(fullSizeBitmap,(v.height / density).toInt(), (v.width / density).toInt())
   // fullSizeBitmap?.recycle()
    return fullSizeBitmap
}
@Keep
fun View.capture(config: Bitmap.Config = Bitmap.Config.ARGB_8888, width: Int, height: Int): Bitmap? {
    val v = this
    if (v.width <= 0 || v.height <= 0)
        return null
    val fullSizeBitmap = Bitmap.createBitmap(width, height, config)
    val c = Canvas(fullSizeBitmap)
    v.layout(v.left, v.top, v.right, v.bottom)
    v.draw(c)
    return fullSizeBitmap
}

/**
 * Tries to focus this view and show the soft input window for it.
 *
 *  @param flags Provides additional operating flags to be used with InputMethodManager.showSoftInput().
 *  Currently may be 0, SHOW_IMPLICIT or SHOW_FORCED.
 */
@Keep
fun View.showKeyboard(flags: Int = InputMethodManager.SHOW_IMPLICIT) {
    ShowKeyboard(this, flags).post()
}

/**
 * Hides the soft input window.
 */
@Keep
fun View.hideKeyboard() {
    val imm = (context.getSystemService(Context.INPUT_METHOD_SERVICE) ?: return)
            as InputMethodManager

    imm.hideSoftInputFromWindow(windowToken, 0)
}
@Keep
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
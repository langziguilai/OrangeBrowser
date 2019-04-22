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

const val FAST_ANIMATION = 200L
const val NORMAL_ANIMATION = 300L
const val SLOW_ANIMATION = 10000L
val DEFAULT_INTERPOLATOR=AccelerateDecelerateInterpolator()

fun View.isShowing(): Boolean {
    return this.visibility == View.VISIBLE
}

fun View.isHidden(): Boolean {
    return this.visibility == View.GONE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

//在layout之后调用：可以获取View的Height,Width等等属性
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
fun ViewGroup.forEach(action: (View) -> Unit) {
    for (index in 0 until childCount) {
        action(getChildAt(index))
    }
}

//截图
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
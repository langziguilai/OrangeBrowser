package com.dev.orangebrowser.view

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import com.dev.util.Keep

@Keep
class LongClickFrameLayout :FrameLayout{
    constructor(context: Context) : this(context,null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    private val longClickPoint:Point= Point()
    private val runnable= Runnable { }
    private var longClickListener: LongClickListener =
        LongClickListener(
            runnable = runnable,
            view = this,
            point = longClickPoint
        )
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        longClickListener.onTouch(ev)
        return super.dispatchTouchEvent(ev)
    }
    fun getLongClickPosition(): Point {
        return longClickPoint
    }
}

class LongClickListener(var view: View, var runnable: Runnable, var point: Point){
    /**
     * 上一次点击的的坐标
     */
    private var lastX: Float = 0.toFloat()
    private var lastY: Float = 0.toFloat()
    /**
     * 长按坐标
     */
    private val longPressX: Float = 0.toFloat()
    private val longPressY: Float = 0.toFloat()
    /**
     * 是否移动
     */
    private var isMove: Boolean = false
    /**
     * 滑动的阈值
     */
    private val TOUCH_SLOP = 20
    fun onTouch(event: MotionEvent){
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isMove = false
                lastX = x
                lastY = y
                //设置初始点的位置
                point.set(x.toInt(),y.toInt())
                view.postDelayed(runnable, ViewConfiguration.getLongPressTimeout().toLong())
            }
            MotionEvent.ACTION_MOVE -> {
                if (!isMove) {
                    if (Math.abs(lastX - x) > TOUCH_SLOP || Math.abs(lastY - y) > TOUCH_SLOP) {
                        //移动超过了阈值，表示移动了
                        isMove = true
                        //移除runnable
                        view.removeCallbacks(runnable)
                    }
                }
            }
            MotionEvent.ACTION_UP -> view.removeCallbacks(runnable)
        }
    }
}
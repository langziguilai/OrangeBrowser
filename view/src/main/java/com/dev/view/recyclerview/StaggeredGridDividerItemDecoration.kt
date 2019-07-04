package com.dev.view.recyclerview

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dev.util.Keep
import com.dev.util.KeepNameIfNecessary

@Keep
class StaggeredGridDividerItemDecoration(private val dividerWidth:Int,@ColorInt  color:Int):RecyclerView.ItemDecoration(){
    private val mPaint: Paint?
    var firstFullSizeIndexForFirstRow:Int=-1
    init {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mPaint.color = color
        mPaint.style = Paint.Style.FILL
    }

    //在刷新的时候需要重置
    fun resetFirstFullSizeIndexForFirstRow(){
        firstFullSizeIndexForFirstRow=-1
    }
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        if (isMustNotFirstRow(parent,view)){
            outRect.top= dividerWidth
        }else{
            val position=parent.getChildAdapterPosition(view)
            if(isFullSpan(view)){
                firstFullSizeIndexForFirstRow=position
                //如果第一个不是full span，需要设置top
                if (position!=0){
                    outRect.top= dividerWidth
                }else{
                    outRect.top= 0
                }
            }else{
                //如果full span已经设置
               if (firstFullSizeIndexForFirstRow!=-1){
                   if(position<firstFullSizeIndexForFirstRow){
                       outRect.top= 0
                   }else{
                       outRect.top= dividerWidth
                   }
               }else{//未设置的时候，全部为0
                   outRect.top= 0
               }
            }

        }
        if (!isFullSpan(view)){
            val spanCount=getSpanCount(parent)
            val spanIndex=getColumn(view)
            val eachWidth=(spanCount - 1) * dividerWidth / spanCount
            val dl=dividerWidth - eachWidth
            val left = spanIndex % spanCount * dl
            val right = eachWidth - left
            outRect.left=left
            outRect.right=right
        }
    }

    private fun isMustNotFirstRow(parent:RecyclerView, child: View):Boolean{
        val spanCount=getSpanCount(parent)
        val position=parent.getChildLayoutPosition(child)
        return position>=spanCount
    }
    private fun getColumn(child: View):Int{
        val lp=  child.layoutParams as StaggeredGridLayoutManager.LayoutParams
        return lp.spanIndex
    }
    private fun getSpanCount(parent: RecyclerView): Int {
        // 列数
        var spanCount = -1
        val layoutManager = parent.layoutManager
        if (layoutManager is StaggeredGridLayoutManager) {
            spanCount = layoutManager
                .spanCount
        }
        return spanCount
    }
    private fun isFullSpan(view: View):Boolean{
        val lp=  view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        return lp.isFullSpan
    }


    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        draw(c, parent)
    }


    //绘制横向 item 分割线
    private fun draw(canvas: Canvas, parent: RecyclerView) {
        val childSize = parent.childCount
        for (i in 0 until childSize) {
            val child = parent.getChildAt(i)
            val layoutParams = child.layoutParams as RecyclerView.LayoutParams

            //画水平分隔线
            var left = child.left
            var right = child.right
            var top = child.bottom + layoutParams.bottomMargin
            var bottom = top + dividerWidth
            if (mPaint != null) {
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
            }
            //画垂直分割线
            top = child.top
            bottom = child.bottom + dividerWidth
            left = child.right + layoutParams.rightMargin
            right = left + dividerWidth
            if (mPaint != null) {
                canvas.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), mPaint)
            }
        }
    }
}
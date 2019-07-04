package com.dev.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.util.Keep

@Keep
class ListView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context,attrs,defStyleAttr) {
    private var  reverse:Boolean
    private var  direction:Int=RecyclerView.VERTICAL
    init {
        context.obtainStyledAttributes(attrs, R.styleable.ListView).apply {
            reverse = getBoolean(R.styleable.ListView_lv_reverse, false)
            val isVertical=getBoolean(R.styleable.ListView_lv_vertical,false)
            if (!isVertical){
                direction=RecyclerView.HORIZONTAL
            }
            recycle()
        }
        //设置layoutManager
        layoutManager=LinearLayoutManager(context,direction,reverse)
    }
}

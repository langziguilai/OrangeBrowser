package com.dev.view

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev.util.DensityUtil
import com.dev.util.Keep
import com.dev.view.recyclerview.GridDividerItemDecoration

@Keep
class GridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context,attrs,defStyleAttr) {
    private var  column:Int
    private var  reverse:Boolean
    private var  direction:Int=RecyclerView.VERTICAL
    private var  horizontalSpace:Float
    private var  verticalSpace:Float
    private var  spaceColor:Int
    init {
        context.obtainStyledAttributes(attrs, R.styleable.GridView).apply {
            column = getInt(R.styleable.GridView_gv_column, 0)
            reverse = getBoolean(R.styleable.GridView_gv_reverse, false)
            val isVertical=getBoolean(R.styleable.GridView_gv_vertical,true)
            if (!isVertical){
                direction=RecyclerView.HORIZONTAL
            }
            verticalSpace=getDimension(R.styleable.GridView_gv_vertical_space,DensityUtil.dip2px(context,10f).toFloat())
            horizontalSpace=getDimension(R.styleable.GridView_gv_horizon_space,DensityUtil.dip2px(context,10f).toFloat())
            spaceColor=getColor(R.styleable.GridView_gv_space_color,resources.getColor(android.R.color.transparent))
            recycle()
        }
        //设置layoutManager
        layoutManager=GridLayoutManager(context,column,direction,reverse)
        addItemDecoration(GridDividerItemDecoration(horizontalSpace.toInt(),verticalSpace.toInt(),spaceColor))
    }
}

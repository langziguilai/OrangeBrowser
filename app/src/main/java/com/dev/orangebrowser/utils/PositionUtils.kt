package com.dev.orangebrowser.utils

import android.content.Context
import com.dev.util.DensityUtil

object PositionUtils {
    var offSet: Int = -1
    fun initOffSet(context:Context){
        offSet = DensityUtil.dip2px(context, 20f)
    }
    //计算左边的距离
     fun calculateRecyclerViewLeftMargin(containerWidth: Int, childWidth: Int, x: Int): Int {
        return if (x - childWidth - offSet > 0) {
            x - childWidth - offSet
        } else {
            x + offSet
        }
    }

    //计算左边的距离
     fun calculateRecyclerViewTopMargin(containerHeight: Int, childHeight: Int, y: Int): Int {
        return when {
            y + childHeight / 2 + offSet > containerHeight -> containerHeight - childHeight - offSet
            y - childHeight / 2 - offSet < 0 -> offSet
            else -> y - childHeight / 2
        }
    }
}
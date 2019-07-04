package com.dev.view

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.dev.util.Keep

@Keep
class IconfontTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    AppCompatTextView(context, attrs, defStyleAttr) {

    init {
        init()
    }

    fun init(fontPath:String="iconfont.ttf") {
        val iconfont = Typeface.createFromAsset(resources.assets,fontPath )
        this.typeface = iconfont
    }

}

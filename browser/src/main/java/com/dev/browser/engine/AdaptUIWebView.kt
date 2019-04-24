package com.dev.browser.engine

import android.content.Context
import android.util.AttributeSet
import android.webkit.WebView
import com.dev.util.DensityUtil

//使用application的context，防止内存泄漏
open class AdaptUIWebView : WebView {
    private var mApplicationContext: Context? = null

    constructor(context: Context) : super(context) {
        mApplicationContext = context.applicationContext
        DensityUtil.resetDensity(mApplicationContext!!)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        mApplicationContext = context.applicationContext
        DensityUtil.resetDensity(mApplicationContext!!)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mApplicationContext = context.applicationContext
        DensityUtil.resetDensity(mApplicationContext!!)
    }


    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    ) {
        mApplicationContext = context.applicationContext
        DensityUtil.resetDensity(mApplicationContext!!)
    }

    override fun setOverScrollMode(mode: Int) {
        super.setOverScrollMode(mode)
        //DensityUtil.resetDensity(mApplicationContext.getApplicationContext());
    }
}
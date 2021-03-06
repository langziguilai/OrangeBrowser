package com.dev.browser.engine.system

import android.content.Context
import android.util.AttributeSet
import com.dev.browser.adblock.webview.AdblockWebView
import com.dev.util.DensityUtil

//使用application的context，防止内存泄漏
open class AdaptUIWebView : AdblockWebView {
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


//    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
//        context,
//        attrs,
//        defStyleAttr,
//        defStyleRes
//    ) {
//        mApplicationContext = context.applicationContext
//        DensityUtil.resetDensity(mApplicationContext!!)
//    }

}
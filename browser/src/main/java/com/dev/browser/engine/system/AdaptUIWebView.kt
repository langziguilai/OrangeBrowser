package com.dev.browser.engine.system

import android.content.Context
import android.util.AttributeSet
import com.dev.browser.adblock.webview.AdblockWebView
import com.dev.util.DensityUtil
import com.dev.util.Keep

//使用application的context，防止内存泄漏
@Keep
open class AdaptUIWebView : AdblockWebView {

    constructor(context: Context) : super(context) {
        DensityUtil.resetDensity()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        DensityUtil.resetDensity()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        DensityUtil.resetDensity()
    }

    override fun setOverScrollMode(mode: Int) {
        super.setOverScrollMode(mode)
        DensityUtil.resetDensity()
    }
}
package com.dev.browser.concept

const val REDIRECT_TO_APP_YES=1
const val REDIRECT_TO_APP_NO=2
const val REDIRECT_TO_APP_ASK=3

object BrowserSetting {
    @JvmField
    var RedirectToApp:Int=REDIRECT_TO_APP_ASK
    @JvmField
    var ShouldUseCacheMode:Boolean=false
}
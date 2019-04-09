package com.dev.orangebrowser.browser

import android.content.Context
import android.webkit.WebView
import com.dev.orangebrowser.view.NestedWebView
import java.util.*

class Session(
    var initialUrl: String="",
    var isHome:Boolean=true, //是否在首页，分为：浏览器页面和首页
    val private: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
    val context:Context
){
    val webView: WebView by lazy {
        NestedWebView(context)
    }
    val visited:Boolean    //是否已经访问WebView了
        get() {
            return webView.url!=null
        }

    fun visit(url:String){
        webView.loadUrl(url)
    }
    companion object {
        fun NewSession(url: String, private:Boolean,context:Context):Session{
            return Session(url,private,context=context)
        }
        fun DefaultSession(context:Context):Session{
            return Session(context = context)
        }
    }
}
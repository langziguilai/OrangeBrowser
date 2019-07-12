package com.dev.browser.concept

abstract class InterceptResource(var link:String="", var referer:String="")
interface InterceptResourceListener{
    fun onResourceDeteceted(resource: InterceptResource)
}
class ImageInterceptResource(link:String, referer:String=""):InterceptResource(link,referer)
class MediaInterceptResource(link:String, var poster:String="", referer:String=""):InterceptResource(link,referer)
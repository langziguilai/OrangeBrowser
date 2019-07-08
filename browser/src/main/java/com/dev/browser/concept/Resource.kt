package com.dev.browser.concept

abstract class Resource(var link:String="",var referer:String="")
class ImageResource(link:String,referer:String=""):Resource(link,referer)
class MediaResource(link:String,var poster:String="",referer:String=""):Resource(link,referer)
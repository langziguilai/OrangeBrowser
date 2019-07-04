package com.dev.base.extension

import com.dev.util.Keep

@Keep
fun String.substringWithLen(start:Int,length:Int):String{
    return substring(start,start+length)
}
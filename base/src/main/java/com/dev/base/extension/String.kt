package com.dev.base.extension

fun String.substringWithLen(start:Int,length:Int):String{
    return substring(start,start+length)
}
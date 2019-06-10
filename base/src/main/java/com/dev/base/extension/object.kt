package com.dev.base.extension


fun Any.getProperty(name:String):Any{
    val filed= this.javaClass.getDeclaredField(name).apply {
        this.isAccessible=true
    }
    return filed.get(this)
}
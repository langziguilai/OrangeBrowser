package com.dev.base.extension


fun Any.getProperty(name:String):Any{
    val filed= this.javaClass.getDeclaredField(name).apply {
        this.isAccessible=true
    }
    return filed.get(this)
}

fun Any.excute(name:String):Any{
    val method=this.javaClass.getDeclaredMethod(name).apply {
        this.isAccessible=true
    }
    return method.invoke(this)
}
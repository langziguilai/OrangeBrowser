package com.dev.base.extension

import com.dev.util.Keep

@Keep
fun Any.getProperty(name:String):Any{
    val filed= this.javaClass.getDeclaredField(name).apply {
        this.isAccessible=true
    }
    return filed.get(this)
}
@Keep
fun Any.excute(name:String):Any{
    val method=this.javaClass.getDeclaredMethod(name).apply {
        this.isAccessible=true
    }
    return method.invoke(this)
}
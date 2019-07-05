package com.dev.orangebrowser.extension

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.dev.orangebrowser.AndroidApplication
import com.dev.orangebrowser.data.model.ApplicationData
import com.dev.orangebrowser.di.ApplicationComponent

val AppCompatActivity.appComponent: ApplicationComponent
    get() = (applicationContext as AndroidApplication).appComponent

val AppCompatActivity.appDataForActivity: ApplicationData?
    get() = (applicationContext as AndroidApplication).applicationData

val AppCompatActivity.myApplication:AndroidApplication
    get() = (applicationContext as AndroidApplication)

fun Activity.getColor(id:Int):Int{
    return resources.getColor(id)
}

fun Activity.getSpBool(id:Int,defaultValue:Boolean=false):Boolean{
    return getPreferences(Context.MODE_PRIVATE).getBoolean(getString(id),defaultValue)
}
fun Activity.getSpString(id:Int,defaultValue:String=""):String{
    return getPreferences(Context.MODE_PRIVATE).getString(getString(id),defaultValue) ?: ""
}
fun Activity.getSpFloat(id:Int,defaultValue:Float=0f):Float{
    return getPreferences(Context.MODE_PRIVATE).getFloat(getString(id),defaultValue)
}
fun Activity.getSpInt(id:Int,defaultValue:Int=0):Int{
    return getPreferences(Context.MODE_PRIVATE).getInt(getString(id),defaultValue)
}
fun Activity.getSpLong(id:Int,defaultValue:Long=0):Long{
    return getPreferences(Context.MODE_PRIVATE).getLong(getString(id),defaultValue)
}
fun Activity.getSpLong(id:Int,defaultValue:MutableSet<String> = HashSet()):MutableSet<String>{
    return getPreferences(Context.MODE_PRIVATE).getStringSet(getString(id),defaultValue) ?: HashSet()
}
fun Activity.setSpBool(id:Int,value:Boolean=false){
    getPreferences(Context.MODE_PRIVATE).edit().apply {
        this.putBoolean(getString(id),value)
    }.apply()
}
fun Activity.setSpString(id:Int,value:String=""){
    getPreferences(Context.MODE_PRIVATE).edit().apply {
        this.putString(getString(id),value)
    }.apply()
}
fun Activity.setSpFloat(id:Int,value:Float=0f){
    getPreferences(Context.MODE_PRIVATE).edit().apply {
        this.putFloat(getString(id),value)
    }.apply()
}
fun Activity.setSpInt(id:Int,value:Int=0){
    getPreferences(Context.MODE_PRIVATE).edit().apply {
        this.putInt(getString(id),value)
    }.apply()
}
fun Activity.setSpLong(id:Int,value:Long=0){
    getPreferences(Context.MODE_PRIVATE).edit().apply {
        this.putLong(getString(id),value)
    }.apply()
}
fun Activity.setSpLong(id:Int,value:MutableSet<String> = HashSet()){
    getPreferences(Context.MODE_PRIVATE).edit().apply {
        this.putStringSet(getString(id),value)
    }.apply()
}
package com.dev.orangebrowser.extension

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.dev.orangebrowser.AndroidApplication
import com.dev.orangebrowser.bloc.host.MainActivity
import com.dev.orangebrowser.data.model.ApplicationData
import com.dev.orangebrowser.data.model.Theme
import com.dev.orangebrowser.di.ApplicationComponent

val androidx.fragment.app.Fragment.appComponent: ApplicationComponent
            get() = (requireContext().applicationContext as AndroidApplication).appComponent



val androidx.fragment.app.Fragment.appData: ApplicationData
    get() = (requireContext().applicationContext as AndroidApplication).applicationData

val androidx.fragment.app.Fragment.RouterActivity: MainActivity?
    get(){
        if (activity is MainActivity){
            return activity as MainActivity
        }
        return null
    }

val androidx.fragment.app.Fragment.application:AndroidApplication
    get() = requireContext().applicationContext as AndroidApplication

fun androidx.fragment.app.Fragment.getStringByResId(id:Int):String{
    return requireContext().resources.getString(id)
}
fun androidx.fragment.app.Fragment.getColor(id:Int):Int{
    return requireContext().resources.getColor(id)
}

fun androidx.fragment.app.Fragment.getSharedPreference():SharedPreferences{
    return requireActivity().getPreferences(MODE_PRIVATE)
}
fun androidx.fragment.app.Fragment.getSpBool(id:Int,defaultValue:Boolean=false):Boolean{
    return requireActivity().getPreferences(MODE_PRIVATE).getBoolean(getString(id),defaultValue)
}
fun androidx.fragment.app.Fragment.getSpString(id:Int,defaultValue:String=""):String{
    return requireActivity().getPreferences(MODE_PRIVATE).getString(getString(id),defaultValue) ?: ""
}
fun androidx.fragment.app.Fragment.getSpFloat(id:Int,defaultValue:Float=0f):Float{
    return requireActivity().getPreferences(MODE_PRIVATE).getFloat(getString(id),defaultValue)
}
fun androidx.fragment.app.Fragment.getSpInt(id:Int,defaultValue:Int=0):Int{
    return requireActivity().getPreferences(MODE_PRIVATE).getInt(getString(id),defaultValue)
}
fun androidx.fragment.app.Fragment.getSpLong(id:Int,defaultValue:Long=0):Long{
    return requireActivity().getPreferences(MODE_PRIVATE).getLong(getString(id),defaultValue)
}
fun androidx.fragment.app.Fragment.getSpLong(id:Int,defaultValue:MutableSet<String> = HashSet()):MutableSet<String>{
    return requireActivity().getPreferences(MODE_PRIVATE).getStringSet(getString(id),defaultValue) ?: HashSet()
}
fun androidx.fragment.app.Fragment.setSpBool(id:Int,value:Boolean=false){
     requireActivity().getPreferences(MODE_PRIVATE).edit().apply {
         this.putBoolean(getString(id),value)
     }.apply()
}
fun androidx.fragment.app.Fragment.setSpString(id:Int,value:String=""){
    requireActivity().getPreferences(MODE_PRIVATE).edit().apply {
        this.putString(getString(id),value)
    }.apply()
}
fun androidx.fragment.app.Fragment.setSpFloat(id:Int,value:Float=0f){
    requireActivity().getPreferences(MODE_PRIVATE).edit().apply {
        this.putFloat(getString(id),value)
    }.apply()
}
fun androidx.fragment.app.Fragment.setSpInt(id:Int,value:Int=0){
    requireActivity().getPreferences(MODE_PRIVATE).edit().apply {
        this.putInt(getString(id),value)
    }.apply()
}
fun androidx.fragment.app.Fragment.setSpLong(id:Int,value:Long=0){
    requireActivity().getPreferences(MODE_PRIVATE).edit().apply {
        this.putLong(getString(id),value)
    }.apply()
}
fun androidx.fragment.app.Fragment.setSpLong(id:Int,value:MutableSet<String> = HashSet()){
    requireActivity().getPreferences(MODE_PRIVATE).edit().apply {
        this.putStringSet(getString(id),value)
    }.apply()
}
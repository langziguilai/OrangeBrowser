package com.dev.orangebrowser.data.model

import android.content.Context
import android.graphics.Color
import com.dev.base.extension.loadJsonArray
import com.dev.orangebrowser.R

data class Theme(
    var colorPrimary: Int,
    var colorPrimaryDark: Int,
    var colorPrimaryActive: Int,
    var colorPrimaryDisable: Int,
    var colorAccent: Int
) {
    companion object {
        fun defaultTheme(context: Context):Theme {
            return Theme(
                colorPrimary = context.resources.getColor(R.color.colorPrimary),
                colorPrimaryDark = context.resources.getColor(R.color.colorPrimaryDark),
                colorAccent = context.resources.getColor(R.color.colorAccent),
                colorPrimaryActive = context.resources.getColor(R.color.colorPrimaryActive),
                colorPrimaryDisable = context.resources.getColor(R.color.colorPrimaryDisable))
        }
    }
}

data class ThemeSource(
    var name:String,  //名称
    var active:Boolean=false,
    var colorPrimary: String,
    var colorPrimaryDark: String,
    var colorPrimaryActive: String,
    var colorPrimaryDisable: String,
    var colorAccent: String
){
    //转换为Theme
    fun toTheme():Theme{
        return Theme(
            colorPrimary= Color.parseColor(colorPrimary),
            colorPrimaryDark= Color.parseColor(colorPrimaryDark),
            colorPrimaryActive= Color.parseColor(colorPrimaryActive),
            colorPrimaryDisable= Color.parseColor(colorPrimaryDisable),
            colorAccent= Color.parseColor(colorAccent)
        )
    }

}

data class ThemeSources(var themeSources:List<ThemeSource>){
    //获取使用的ThemeSource
    fun getActiveThemeSource():ThemeSource?{
        return themeSources.find { it.active  }
    }
    //激活Theme
    fun actvieThemeSource(name:String){
        themeSources.forEach { it.active=false }
        themeSources.find { it.name == name }?.active=true
    }
    companion object {
        fun loadThemeSources(context:Context):ThemeSources{
            return ThemeSources(themeSources =context.loadJsonArray<ThemeSource>("themes.json") )
        }
    }
}
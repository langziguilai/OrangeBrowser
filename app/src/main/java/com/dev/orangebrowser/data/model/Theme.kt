package com.dev.orangebrowser.data.model

import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
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
    var name: String,  //名称
    var active: Boolean = false,
    var colorPrimary: String,
    var colorPrimaryDark: String,
    var colorAccent: String,
    var colorPrimaryActive: String,
    var colorPrimaryDisable: String
) : Parcelable {
    //转换为Theme
    fun toTheme(): Theme {
        return Theme(
            colorPrimary = Color.parseColor(colorPrimary),
            colorPrimaryDark = Color.parseColor(colorPrimaryDark),
            colorPrimaryActive = Color.parseColor(colorPrimaryActive),
            colorPrimaryDisable = Color.parseColor(colorPrimaryDisable),
            colorAccent = Color.parseColor(colorAccent)
        )
    }

    constructor(source: Parcel) : this(
        source.readString(),
        1 == source.readInt(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeInt((if (active) 1 else 0))
        writeString(colorPrimary)
        writeString(colorPrimaryDark)
        writeString(colorPrimaryActive)
        writeString(colorPrimaryDisable)
        writeString(colorAccent)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ThemeSource> = object : Parcelable.Creator<ThemeSource> {
            override fun createFromParcel(source: Parcel): ThemeSource = ThemeSource(source)
            override fun newArray(size: Int): Array<ThemeSource?> = arrayOfNulls(size)
        }
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
            val themes=context.loadJsonArray("themes.json",ThemeSource::class.java)
            return ThemeSources(themeSources = themes)
        }
    }
}
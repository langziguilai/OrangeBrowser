package com.dev.orangebrowser.data.model

import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import com.dev.base.extension.loadJsonArray
import com.dev.orangebrowser.R

data class Theme(
    var name:String,
    var colorPrimary: Int,
    var colorPrimaryDark: Int,
    var colorPrimaryActive: Int,
    var colorPrimaryDisable: Int,
    var colorBackground:Int,
    var colorAccent: Int
) {

    fun clone():Theme{
        return Theme(
            name=this.name,
            colorPrimary=this.colorPrimary,
            colorAccent = this.colorAccent,
            colorPrimaryDark = this.colorPrimaryDark,
            colorPrimaryActive = this.colorPrimaryActive,
            colorPrimaryDisable = this.colorPrimaryDisable,
            colorBackground = this.colorBackground
        )
    }
    companion object {
        fun defaultTheme(context: Context):Theme {
            return Theme(
                name="default",
                colorPrimary = context.resources.getColor(R.color.colorPrimary),
                colorPrimaryDark = context.resources.getColor(R.color.colorPrimaryDark),
                colorAccent = context.resources.getColor(R.color.colorAccent),
                colorPrimaryActive = context.resources.getColor(R.color.colorPrimaryActive),
                colorPrimaryDisable = context.resources.getColor(R.color.colorPrimaryDisable),
                colorBackground = context.resources.getColor(R.color.colorBackground)
                )
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
    var colorPrimaryDisable: String,
    var colorBackground: String
) : Parcelable {
    //转换为Theme
    fun toTheme(): Theme {
        return Theme(
            name=name,
            colorPrimary = Color.parseColor(colorPrimary),
            colorPrimaryDark = Color.parseColor(colorPrimaryDark),
            colorPrimaryActive = Color.parseColor(colorPrimaryActive),
            colorPrimaryDisable = Color.parseColor(colorPrimaryDisable),
            colorAccent = Color.parseColor(colorAccent),
            colorBackground = Color.parseColor(colorBackground)
        )
    }

    constructor(source: Parcel) : this(
        source.readString(),
        1 == source.readInt(),
        source.readString(),
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
        writeString(colorBackground)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ThemeSource> = object : Parcelable.Creator<ThemeSource> {
            override fun createFromParcel(source: Parcel): ThemeSource = ThemeSource(source)
            override fun newArray(size: Int): Array<ThemeSource?> = arrayOfNulls(size)
        }
    }
}

data class ThemeSources(var themeSources:List<ThemeSource>) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createTypedArrayList(ThemeSource.CREATOR))
    //获取使用的ThemeSource
    fun getActiveThemeSource():ThemeSource?{
        return themeSources.find { it.active  }
    }
    //激活Theme
    fun actvieThemeSource(name:String){
        themeSources.forEach { it.active=false }
        themeSources.find { it.name == name }?.active=true
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(themeSources)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ThemeSources> {
        fun loadThemeSources(context:Context):ThemeSources{
            val themes=context.loadJsonArray("themes.json",ThemeSource::class.java)
            return ThemeSources(themeSources = themes)
        }
        override fun createFromParcel(parcel: Parcel): ThemeSources {
            return ThemeSources(parcel)
        }

        override fun newArray(size: Int): Array<ThemeSources?> {
            return arrayOfNulls(size)
        }
    }
}
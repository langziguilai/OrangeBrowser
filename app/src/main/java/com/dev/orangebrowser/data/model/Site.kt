package com.dev.orangebrowser.data.model

import android.os.Parcel
import android.os.Parcelable

data class Site(
    var name: String? = null,
    var description: String? = null,
    var url: String? = null,
    var icon: String? = null,
    var iconResId: Int? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readValue(Int::class.java.classLoader) as Int?
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(description)
        writeString(url)
        writeString(icon)
        writeValue(iconResId)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Site> = object : Parcelable.Creator<Site> {
            override fun createFromParcel(source: Parcel): Site = Site(source)
            override fun newArray(size: Int): Array<Site?> = arrayOfNulls(size)
        }
    }
}

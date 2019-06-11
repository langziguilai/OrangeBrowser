package com.dev.orangebrowser.data.model

import android.os.Parcel
import android.os.Parcelable

data class SimpleImage(var url: String?=null,var path:String?=null, var referer: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(path)
        parcel.writeString(referer)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SimpleImage> {
        override fun createFromParcel(parcel: Parcel): SimpleImage {
            return SimpleImage(parcel)
        }

        override fun newArray(size: Int): Array<SimpleImage?> {
            return arrayOfNulls(size)
        }
    }
}
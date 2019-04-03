package com.dev.orangebrowser.data.model

import android.os.Parcel
import android.os.Parcelable

data class NewsCategory(    var name: String? = null,
                            var id: String? = null) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NewsCategory> {
        override fun createFromParcel(parcel: Parcel): NewsCategory {
            return NewsCategory(parcel)
        }

        override fun newArray(size: Int): Array<NewsCategory?> {
            return arrayOfNulls(size)
        }
    }
}


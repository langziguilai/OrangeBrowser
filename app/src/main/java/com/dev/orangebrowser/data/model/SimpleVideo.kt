package com.dev.orangebrowser.data.model

import android.os.Parcel
import android.os.Parcelable

/**
 * 1.封面地址
 * 2.本地视频路径
 * 3.Referer
 * */
data class SimpleVideo(
    var poster: String? = null,
    var localPoster:String?=null,
    var url: String? = null,
    var path: String? = null,
    var referer: String? = null
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(poster)
        writeString(localPoster)
        writeString(url)
        writeString(path)
        writeString(referer)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SimpleVideo> = object : Parcelable.Creator<SimpleVideo> {
            override fun createFromParcel(source: Parcel): SimpleVideo = SimpleVideo(source)
            override fun newArray(size: Int): Array<SimpleVideo?> = arrayOfNulls(size)
        }
    }
}
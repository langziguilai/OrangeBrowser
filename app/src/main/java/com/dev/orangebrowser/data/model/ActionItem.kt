package com.dev.orangebrowser.data.model

import android.os.Parcel
import android.os.Parcelable

data class ActionItem(var id:Int=-1,var active:Boolean=false, var nameRes:Int, var iconRes:Int) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeByte(if (active) 1 else 0)
        parcel.writeInt(nameRes)
        parcel.writeInt(iconRes)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ActionItem> {
        override fun createFromParcel(parcel: Parcel): ActionItem {
            return ActionItem(parcel)
        }

        override fun newArray(size: Int): Array<ActionItem?> {
            return arrayOfNulls(size)
        }
    }
}
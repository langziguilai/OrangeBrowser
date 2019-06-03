package com.dev.orangebrowser.data.model

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.ArrayMap
import com.dev.orangebrowser.R
import java.util.*
import kotlin.collections.HashMap

const val THEME = "theme"
const val FAVOR_SITES = "favorSites"
const val BOTTOM_MENU_ACTION_ITEMS = "bottomMenuActionItems"
const val TOP_MENU_ACTION_ITEMS = "topMenuActionItems"

//系统配置文件
data class ApplicationData(
    var favorSites: List<Site>,
    var themes: ThemeSources,
    var bottomMenuActionItems: List<ActionItem> = getBottomMenuActionItems(),
    var topMenuActionItems: List<ActionItem> = getTopMenActionItems(),
    var fragmentStateMap: HashMap<String, Bundle> = HashMap()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        LinkedList<Site>(parcel.createTypedArrayList(Site.CREATOR)),
        parcel.readParcelable(ThemeSources::class.java.classLoader)!!,
        LinkedList<ActionItem>(parcel.createTypedArrayList(ActionItem)),
        LinkedList<ActionItem>(parcel.createTypedArrayList(ActionItem)),
        parcel.readSerializable() as HashMap<String, Bundle>
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeTypedList(favorSites)
        parcel.writeParcelable(themes, flags)
        parcel.writeTypedList(bottomMenuActionItems)
        parcel.writeTypedList(topMenuActionItems)
        parcel.writeSerializable(fragmentStateMap)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ApplicationData> {
        override fun createFromParcel(parcel: Parcel): ApplicationData {
            return ApplicationData(parcel)
        }

        override fun newArray(size: Int): Array<ApplicationData?> {
            return arrayOfNulls(size)
        }
    }
}


fun getBottomMenuActionItems(): List<ActionItem> {
    val result = LinkedList<ActionItem>()
    result.add(
        ActionItem(
            nameRes = R.string.forbid_image,
            iconRes = R.string.ic_forbid_image,
            id = R.string.ic_forbid_image
        )
    )
    result.add(ActionItem(nameRes = R.string.privacy, iconRes = R.string.ic_privacy,id = R.string.ic_privacy))
    result.add(
        ActionItem(
            nameRes = R.string.vision,
            iconRes = R.string.ic_normal_screen,
            id = R.string.ic_normal_screen
        )
    )
    result.add(ActionItem(nameRes = R.string.desktop, iconRes = R.string.ic_desktop, id = R.string.ic_desktop))
    result.add(ActionItem(nameRes = R.string.found, iconRes = R.string.ic_found, id = R.string.ic_found))
    result.add(ActionItem(nameRes = R.string.history, iconRes = R.string.ic_history, id = R.string.ic_history))
    result.add(ActionItem(nameRes = R.string.bookmark, iconRes = R.string.ic_bookmark, id = R.string.ic_bookmark))
    result.add(ActionItem(nameRes = R.string.collect, iconRes = R.string.ic_star, id = R.string.ic_star))
    result.add(ActionItem(nameRes = R.string.theme, iconRes = R.string.ic_theme, id = R.string.ic_theme))
    result.add(ActionItem(nameRes = R.string.download, iconRes = R.string.ic_download, id = R.string.ic_download))
    result.add(ActionItem(nameRes = R.string.setting, iconRes = R.string.ic_setting, id = R.string.ic_setting))
    result.add(ActionItem(nameRes = R.string.quit, iconRes = R.string.ic_quit, id = R.string.ic_quit))
    return result
}

fun getTopMenActionItems(): List<ActionItem> {
    val result = LinkedList<ActionItem>()
    return result
}
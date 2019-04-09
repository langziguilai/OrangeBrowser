package com.dev.orangebrowser.data.model

import com.dev.orangebrowser.R
import java.util.*

//系统配置文件
data class ApplicationData(var favorSites:List<Site>,
                           var themes:ThemeSources,
                           var bottomMenuActionItems:List<ActionItem> = getBottomMenuActionItems(),
                           var topMenuActionItems:List<ActionItem> = getTopMenActionItems())


fun getBottomMenuActionItems():List<ActionItem>{
    val result= LinkedList<ActionItem>()
    result.add(ActionItem(nameRes = R.string.forbid_image,iconRes = R.string.ic_forbid_image))
    result.add(ActionItem(nameRes = R.string.privacy,iconRes = R.string.ic_privacy))
    result.add(ActionItem(nameRes = R.string.vision,iconRes = R.string.ic_fullscreen))
    result.add(ActionItem(nameRes = R.string.desktop,iconRes = R.string.ic_desktop))
    result.add(ActionItem(nameRes = R.string.found,iconRes = R.string.ic_found))
    result.add(ActionItem(nameRes = R.string.history,iconRes = R.string.ic_history))
    result.add(ActionItem(nameRes = R.string.bookmark,iconRes = R.string.ic_bookmark))
    result.add(ActionItem(nameRes = R.string.collect,iconRes = R.string.ic_star))
    result.add(ActionItem(nameRes = R.string.theme,iconRes = R.string.ic_theme))
    result.add(ActionItem(nameRes = R.string.download,iconRes = R.string.ic_download))
    result.add(ActionItem(nameRes = R.string.setting,iconRes = R.string.ic_setting))
    result.add(ActionItem(nameRes = R.string.quit,iconRes = R.string.ic_quit))
    return result
}

fun getTopMenActionItems():List<ActionItem>{
    val result=LinkedList<ActionItem>()
    result.add(ActionItem(nameRes = R.string.scan,iconRes = R.string.ic_scan))
    result.add(ActionItem(nameRes = R.string.share,iconRes = R.string.ic_share))
    result.add(ActionItem(nameRes = R.string.read_mode,iconRes = R.string.ic_read))
    result.add(ActionItem(nameRes = R.string.image_mode,iconRes = R.string.ic_image))
    result.add(ActionItem(nameRes = R.string.mark_ad,iconRes = R.string.ic_ad_mark))
    result.add(ActionItem(nameRes = R.string.find_in_page,iconRes = R.string.ic_search))
    result.add(ActionItem(nameRes = R.string.save_resource_offline,iconRes = R.string.ic_save))
    result.add(ActionItem(nameRes = R.string.translation,iconRes = R.string.ic_translate))
    result.add(ActionItem(nameRes = R.string.view_source_code,iconRes = R.string.ic_code))
    result.add(ActionItem(nameRes = R.string.detect_resource,iconRes = R.string.ic_resources_fang))
    result.add(ActionItem(nameRes = R.string.add_to_home_page,iconRes = R.string.ic_store))
    result.add(ActionItem(nameRes = R.string.sky_net,iconRes = R.string.ic_sky_net))
    return result
}
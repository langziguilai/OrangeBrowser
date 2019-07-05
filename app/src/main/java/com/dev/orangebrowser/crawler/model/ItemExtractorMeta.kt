package com.dev.orangebrowser.crawler.model

import com.dev.orangebrowser.crawler.DEFAULT_TEXT_ATTRIBUTE
import com.dev.util.KeepAll

@KeepAll
data class ItemExtractorMeta(
    var selector:String="", //选择器
    var attribute:String= DEFAULT_TEXT_ATTRIBUTE,   //属性
    var script:String=""       //执行的脚本
)
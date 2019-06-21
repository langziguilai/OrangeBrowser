package com.dev.orangebrowser.crawler.model
const val DEFAULT_TEXT_ATTRIBUTE="DEFAULT_TEXT_ATTRIBUTE"
data class ItemExtractorMeta(
    var selector:String="", //选择器
    var attribute:String= DEFAULT_TEXT_ATTRIBUTE,   //属性
    var script:String=""       //执行的脚本
)
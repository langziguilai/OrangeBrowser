package com.dev.orangebrowser.crawler.model

data class Category(
    var name:String="",  //名称
    var link:String="",  //链接
    var icon:String="",  //图标
    var lastUpdate:String="",  //最近更新
    var count:String=""  //文章总数
):Base()
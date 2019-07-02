package com.dev.orangebrowser.crawler.model

import java.util.LinkedList

class Gallery(
    val images:LinkedList<String> = LinkedList(), //图片列表
    val article:Article= Article() //文章属性
):Base()
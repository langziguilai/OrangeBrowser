package com.dev.orangebrowser.crawler.model
import java.util.LinkedList
class Video(
    var videoes: LinkedList<String> = LinkedList(), //视频列表
    val article:Article= Article() //文章属性
):Base()
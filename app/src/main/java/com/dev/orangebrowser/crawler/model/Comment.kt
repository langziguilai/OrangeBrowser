package com.dev.orangebrowser.crawler.model
const val DEFAULT_COMMENT_TYPE=1
const val REPLY_COMMENT_TYPE=1
data class Comment(
    var source: String = "", //文章地址
    var date: String = "",  //创建日期
    var content: String = "", //内容
    var score: String = "", //得分
    var type: Int = DEFAULT_COMMENT_TYPE,//默认(1)为正常类型的评论，（2）为回复类型评论
    var user: User = User(), //评论人
    var repliedUser: User? = null //被回复的人
)
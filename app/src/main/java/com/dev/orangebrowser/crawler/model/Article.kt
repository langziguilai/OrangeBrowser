package com.dev.orangebrowser.crawler.model

open class Article(
     var source:String="", //文章源地址
     var title:String="", //标题
     var author:User=User(), //作者
     var date:String="",  //创建日期
     var cover:String="", //封面
     var abstract:String="", //摘要
     var score:String="", //得分
     var content:String="", //内容
     var viewCount:String="" //浏览数
)
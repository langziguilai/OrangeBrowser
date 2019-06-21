package com.dev.orangebrowser.crawler.extractor

import com.dev.orangebrowser.crawler.model.ItemExtractorMeta
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class ListItemExtractor<T>(
    var listSelector:String?,
    var listItemMetaMap:Map<String, ItemExtractorMeta?>,
    var nextPageMeta: ItemExtractorMeta?=null,
    var mapper:(Map<String,String>)->T
):BaseExtractor(){
    //获取列表
   fun extractList(document:Document): List<T> {
      return extractList(element=document,listSelector = listSelector,metaMap = listItemMetaMap,mapper = mapper)
   }
    //获取下一页的地址
   fun extractNextPageUrl(element:Element):String{
       return extractItem(element,nextPageMeta)
   }
}
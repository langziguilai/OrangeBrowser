package com.dev.orangebrowser.crawler.extractor

import com.dev.orangebrowser.crawler.model.Category
import com.dev.orangebrowser.crawler.model.ItemExtractorMeta
import com.dev.orangebrowser.crawler.model.User
import org.jsoup.nodes.Element

//解析Category详情
class CategoryExtractor(
     var nameMeta: ItemExtractorMeta?=null,
     var linkMeta: ItemExtractorMeta?=null,
     var iconMeta: ItemExtractorMeta?=null,
     var lastUpdateMeta: ItemExtractorMeta?=null,
     var countMeta: ItemExtractorMeta?=null
):BaseExtractor(){
    fun extract(element: Element,oldCategory:Category): Category {
        if (oldCategory.name.isBlank()){
            extractItem(element,nameMeta).apply {
                if (this.isNotBlank()) oldCategory.name=this
            }
        }
        if (oldCategory.link.isBlank()){
            extractItem(element,linkMeta).apply {
                if (this.isNotBlank()) oldCategory.link=this
            }
        }
        if (oldCategory.icon.isBlank()){
            extractItem(element,iconMeta).apply {
                if (this.isNotBlank()) oldCategory.icon=this
            }
        }
        if (oldCategory.lastUpdate.isBlank()){
            extractItem(element,lastUpdateMeta).apply {
                if (this.isNotBlank()) oldCategory.lastUpdate=this
            }
        }
        if (oldCategory.count.isBlank()){
            extractItem(element,countMeta).apply {
                if (this.isNotBlank()) oldCategory.count=this
            }
        }
        return oldCategory
    }
}
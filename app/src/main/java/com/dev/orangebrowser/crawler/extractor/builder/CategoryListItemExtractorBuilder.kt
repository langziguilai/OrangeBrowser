package com.dev.orangebrowser.crawler.extractor.builder

import com.dev.orangebrowser.crawler.extractor.ListItemExtractor
import com.dev.orangebrowser.crawler.model.Category
import com.dev.orangebrowser.crawler.model.ItemExtractorMeta

class CategoryListItemExtractorBuilder {
      var nameMeta: ItemExtractorMeta? =null
     var linkMeta: ItemExtractorMeta? =null
     var iconMeta: ItemExtractorMeta? =null
     var lastUpdateMeta: ItemExtractorMeta? =null
     var countMeta: ItemExtractorMeta? =null
     var nextPageMeta: ItemExtractorMeta? =null
     var listSelector: String? = null

    fun build(): ListItemExtractor<Category> {
        val listItemMetaMap=HashMap<String,ItemExtractorMeta?>()
        listItemMetaMap[NAME]=nameMeta
        listItemMetaMap[LINK]=linkMeta
        listItemMetaMap[ICON]=iconMeta
        listItemMetaMap[LAST_UPDATE]=lastUpdateMeta
        listItemMetaMap[COUNT]=countMeta
        val mapper=fun(map:Map<String,String>):Category{
            val category= Category()
            map[NAME]?.apply {
                category.name=this
            }
            map[LINK]?.apply {
                category.link=this
            }
            map[ICON]?.apply {
                category.icon=this
            }
            map[LAST_UPDATE]?.apply {
                category.lastUpdate=this
            }
            map[COUNT]?.apply {
                category.count=this
            }
            return category
        }
        return ListItemExtractor(listSelector =listSelector,
            nextPageMeta = nextPageMeta,
            listItemMetaMap = listItemMetaMap,
            mapper = mapper)
    }
    companion object{
        const val NAME="id"
        const val LINK="link"
        const val ICON="icon"
        const val LAST_UPDATE="last_update"
        const val COUNT="count"
    }
}
package com.dev.orangebrowser.crawler.extractor

import com.dev.orangebrowser.crawler.model.DEFAULT_TEXT_ATTRIBUTE
import com.dev.orangebrowser.crawler.model.ItemExtractorMeta
import org.jsoup.nodes.Element

const val EMPTY = ""

open class BaseExtractor {
    /**
     * 1：获取元素
     * 2：获取属性
     * 3：如果有必要，执行代码
     * */
    fun extractItem(element: Element, meta: ItemExtractorMeta?): String {
        if (meta==null) return EMPTY
        if (meta.selector.isBlank()) return EMPTY
        val elements = element.select(meta.selector)
        if (elements.isEmpty()) return EMPTY
        if (meta.attribute.isBlank()) return EMPTY
        val result = if (meta.attribute == DEFAULT_TEXT_ATTRIBUTE) {
            elements[0].text()
        } else {
            elements[0].attr(meta.attribute)
        }
        if (result.isNotBlank() && meta.script.isNotBlank()) {
            return executeScript(result, meta.script)
        }
        return result
    }
    /**
     * 1：获取元素列表
     * 2：获取属性并放置到map中
     * 3：将map转换为对应的结果
     * */
    fun <R> extractList(
        element: Element,
        listSelector: String,
        metaMap: Map<String, ItemExtractorMeta?>,
        mapper: (Map<String, String>) -> R
    ): List<R> {
        return element.select(listSelector).map {
            val map = HashMap<String, String>()
            for (entry in metaMap) {
                entry.value?.apply {
                    map[entry.key]=extractItem(it,this)
                }
            }
            mapper(map)
        }
    }

    fun executeScript(source: String, script: String): String {
        return EMPTY
    }
}
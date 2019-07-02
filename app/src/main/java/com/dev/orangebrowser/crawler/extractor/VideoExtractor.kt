package com.dev.orangebrowser.crawler.extractor

import com.dev.orangebrowser.crawler.model.*
import org.jsoup.nodes.Element

class VideoExtractor(
    var videoMeta: ItemExtractorMeta?=null,
    var articleExtractor: ArticleExtractor
) : BaseExtractor() {
    fun extract(element: Element, oldVideo: Video) {
        extractItem(element,videoMeta).apply {
            if(this.isNotBlank()){
                oldVideo.videoes.add(this)
            }
        }
        articleExtractor.extract(element,oldVideo.article)
    }

    fun extractNextPageUrl(element: Element): String {
        return articleExtractor.extractNextPageUrl(element)
    }
    companion object {
        fun build(
            videoMeta: ItemExtractorMeta?=null,
            titleMeta: ItemExtractorMeta? = null,
            dateMeta: ItemExtractorMeta? = null,
            coverMeta: ItemExtractorMeta? = null,
            abstractMeta: ItemExtractorMeta? = null,
            scoreMeta: ItemExtractorMeta? = null,
            contentMeta: ItemExtractorMeta? = null,
            viewCountMeta: ItemExtractorMeta? = null,
            nextPageMeta: ItemExtractorMeta? = null,
            authorIdMeta: ItemExtractorMeta? = null,
            authorNameMeta: ItemExtractorMeta? = null,
            authorAvatarMeta: ItemExtractorMeta? = null
        ): VideoExtractor {
            val articleExtractor= ArticleExtractor(
                titleMeta,
                dateMeta,
                coverMeta,
                abstractMeta,
                scoreMeta,
                contentMeta,
                viewCountMeta,
                nextPageMeta,
                authorIdMeta,
                authorNameMeta,
                authorAvatarMeta
            )
            return VideoExtractor(videoMeta,articleExtractor)
        }
    }
}
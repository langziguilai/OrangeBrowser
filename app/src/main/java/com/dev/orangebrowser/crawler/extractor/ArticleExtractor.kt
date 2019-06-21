package com.dev.orangebrowser.crawler.extractor

import com.dev.orangebrowser.crawler.model.Article
import com.dev.orangebrowser.crawler.model.ItemExtractorMeta
import com.dev.orangebrowser.crawler.model.User
import org.jsoup.nodes.Element

class ArticleExtractor(
    var titleMeta: ItemExtractorMeta? = null,
    var dateMeta: ItemExtractorMeta? = null,
    var coverMeta: ItemExtractorMeta? = null,
    var abstractMeta: ItemExtractorMeta? = null,
    var scoreMeta: ItemExtractorMeta? = null,
    var contentMeta: ItemExtractorMeta? = null,
    var viewCountMeta: ItemExtractorMeta? = null,
    var nextPageMeta: ItemExtractorMeta? = null,
    var authorIdMeta: ItemExtractorMeta? = null,
    var authorNameMeta: ItemExtractorMeta? = null,
    var authorAvatarMeta: ItemExtractorMeta? = null
) : BaseExtractor() {
    fun extract(element: Element, oldArticle: Article) {
        if (oldArticle.title.isBlank()) {
            extractItem(element, titleMeta).apply {
                if (this.isNotBlank()) oldArticle.title = this
            }
        }
        if (oldArticle.date.isBlank()) {
            extractItem(element, dateMeta).apply {
                if (this.isNotBlank()) oldArticle.date = this
            }
        }
        if (oldArticle.cover.isBlank()) {
            extractItem(element, coverMeta).apply {
                if (this.isNotBlank()) oldArticle.cover = this
            }
        }
        if (oldArticle.abstract.isBlank()) {
            extractItem(element, abstractMeta).apply {
                if (this.isNotBlank()) oldArticle.abstract = this
            }
        }
        if (oldArticle.score.isBlank()) {
            extractItem(element, scoreMeta).apply {
                if (this.isNotBlank()) oldArticle.score = this
            }
        }
        if (oldArticle.viewCount.isBlank()) {
            extractItem(element, viewCountMeta).apply {
                if (this.isNotBlank()) oldArticle.viewCount = this
            }
        }
        if (oldArticle.author.id.isBlank()) {
            extractItem(element, authorIdMeta).apply {
                if (this.isNotBlank()) oldArticle.author.id = this
            }
        }
        if (oldArticle.author.username.isBlank()) {
            extractItem(element, authorNameMeta).apply {
                if (this.isNotBlank()) oldArticle.author.username = this
            }
        }
        if (oldArticle.author.avatar.isBlank()) {
            extractItem(element, authorAvatarMeta).apply {
                if (this.isNotBlank()) oldArticle.author.avatar = this
            }
        }
        //面对article分页的情况，每次都叠加内容
        extractItem(element, contentMeta).apply {
            if (this.isNotBlank()) oldArticle.content = oldArticle.content + this
        }
    }

    fun extractNextPageUrl(element: Element): String {
        return extractItem(element, nextPageMeta)
    }

    companion object {
        fun build(
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
        ): ArticleExtractor {
            return ArticleExtractor(
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
        }
    }
}
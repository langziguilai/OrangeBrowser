package com.dev.orangebrowser.crawler.extractor.builder

import com.dev.orangebrowser.crawler.extractor.ListItemExtractor
import com.dev.orangebrowser.crawler.model.Article
import com.dev.orangebrowser.crawler.model.ItemExtractorMeta
import com.dev.orangebrowser.crawler.model.User

class ArticleListItemExtractorBuilder {
     var titleMeta: ItemExtractorMeta? = null
     var dateMeta: ItemExtractorMeta? = null
     var coverMeta: ItemExtractorMeta? = null
     var abstractMeta: ItemExtractorMeta? = null
     var scoreMeta: ItemExtractorMeta? = null
     var contentMeta: ItemExtractorMeta? = null
     var viewCountMeta: ItemExtractorMeta? = null
     var nextPageMeta: ItemExtractorMeta?=null
      var listSelector: String?=null
     var authorIdMeta: ItemExtractorMeta? = null
     var authorNameMeta: ItemExtractorMeta? = null
     var authorAvatarMeta: ItemExtractorMeta? = null

    fun build(): ListItemExtractor<Article> {
        val listItemMetaMap = HashMap<String, ItemExtractorMeta?>()
        listItemMetaMap[TITLE] = titleMeta
        listItemMetaMap[DATE] = dateMeta
        listItemMetaMap[COVER] = coverMeta
        listItemMetaMap[ABSTRACT] = abstractMeta
        listItemMetaMap[SCORE] = scoreMeta
        listItemMetaMap[CONTENT] = contentMeta
        listItemMetaMap[VIEW_COUNT] = viewCountMeta
        listItemMetaMap[AUTHOR_ID] = authorIdMeta
        listItemMetaMap[AUTHOR_NAME] = authorNameMeta
        listItemMetaMap[AUTHOR_AVATAR] = authorAvatarMeta
        val mapper = fun(map: Map<String, String>): Article {
            val article = Article()
            map[TITLE]?.apply {
                article.title = this
            }
            map[DATE]?.apply {
                article.date = this
            }
            map[COVER]?.apply {
                article.cover = this
            }
            map[ABSTRACT]?.apply {
                article.abstract = this
            }
            map[SCORE]?.apply {
                article.score = this
            }
            map[CONTENT]?.apply {
                article.content = this
            }
            map[VIEW_COUNT]?.apply {
                article.viewCount = this
            }
            val author = User()
            map[AUTHOR_ID]?.apply {
                if (this.isNotBlank()) author.id = this
            }
            map[AUTHOR_NAME]?.apply {
                if (this.isNotBlank()) author.username = this
            }
            map[AUTHOR_AVATAR]?.apply {
                if (this.isNotBlank()) author.avatar = this
            }
            article.author = author
            return article
        }
        return ListItemExtractor(
            listSelector = listSelector,
            nextPageMeta = nextPageMeta,
            listItemMetaMap = listItemMetaMap,
            mapper = mapper
        )
    }

    companion object {
        const val TITLE = "title"
        const val DATE = "date"
        const val COVER = "cover"
        const val ABSTRACT = "abstract"
        const val SCORE = "score"
        const val CONTENT = "content"
        const val VIEW_COUNT = "view_count"
        const val AUTHOR_ID = "author_id"
        const val AUTHOR_NAME = "author_name"
        const val AUTHOR_AVATAR = "author_avatar"
    }
}
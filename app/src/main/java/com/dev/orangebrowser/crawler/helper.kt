package com.dev.orangebrowser.crawler

import com.dev.orangebrowser.crawler.extractor.*
import com.dev.orangebrowser.crawler.extractor.builder.ArticleListItemExtractorBuilder
import com.dev.orangebrowser.crawler.extractor.builder.CategoryListItemExtractorBuilder
import com.dev.orangebrowser.crawler.extractor.builder.CommentListItemExtractorBuilder
import com.dev.orangebrowser.crawler.model.Article
import com.dev.orangebrowser.crawler.model.Category
import com.dev.orangebrowser.crawler.model.Comment
import com.google.gson.Gson

fun parseSiteExtractor(metaStr: String): SiteExtractor {
    val meta = Gson().fromJson<CrawlSiteMeta>(metaStr, CrawlSiteMeta::class.java)
    return SiteExtractor(
        categoryListItemExtractorMap = getCategoryListItemExtractorMap(meta.categoryListMetaMap),
        articleListItemExtractorMap = getArticleListItemExtractorMap(meta.articleListMetaMap),
        commentListItemExtractorMap = getCommentListItemExtractorMap(meta.commentListMetaMap),
        articleItemExtractorMap = getArticleExtractorMap(meta.articleMetaMap),
        galleryItemExtractorMap = getGalleryExtractorMap(meta.galleryMetaMap),
        videoItemExtractorMap = getVideoExtractorMap(meta.videoMetaMap)
    )
}

fun getCategoryListItemExtractorMap(meta: Map<String, CategoryListMeta>?): Map<String, ListItemExtractor<Category>> {
    val categoryListItemExtractorMap = HashMap<String, ListItemExtractor<Category>>()
    if (meta==null) return categoryListItemExtractorMap
    for (entry in meta) {
        val categoryListMeta = entry.value
        val categoryListItemExtractor = CategoryListItemExtractorBuilder().apply {
            this.countMeta = categoryListMeta.countMeta
            this.iconMeta = categoryListMeta.iconMeta
            this.lastUpdateMeta = categoryListMeta.lastUpdateMeta
            this.linkMeta = categoryListMeta.linkMeta
            this.nameMeta = categoryListMeta.nameMeta
            this.nextPageMeta = categoryListMeta.nextPageMeta
            this.listSelector = categoryListMeta.listSelector
        }.build()
        categoryListItemExtractor.id = entry.key
        categoryListItemExtractor.nextExtractorId = categoryListMeta.nextExtractorId
        categoryListItemExtractorMap[entry.key] = categoryListItemExtractor
    }
    return categoryListItemExtractorMap
}

fun getArticleListItemExtractorMap(meta: Map<String, ArticleListMeta>?): Map<String, ListItemExtractor<Article>> {
    val articleListItemExtractorMap = HashMap<String, ListItemExtractor<Article>>()
    if (meta==null) return articleListItemExtractorMap
    for (entry in meta) {
        val articleListMeta = entry.value
        val articleListItemExtractor = ArticleListItemExtractorBuilder().apply {
            this.abstractMeta = articleListMeta.abstractMeta
            this.authorAvatarMeta = articleListMeta.authorAvatarMeta
            this.authorNameMeta = articleListMeta.authorNameMeta
            this.authorIdMeta = articleListMeta.authorIdMeta
            this.contentMeta = articleListMeta.contentMeta
            this.coverMeta = articleListMeta.coverMeta
            this.dateMeta = articleListMeta.dateMeta
            this.listSelector = articleListMeta.listSelector
            this.nextPageMeta = articleListMeta.nextPageMeta
            this.scoreMeta = articleListMeta.scoreMeta
            this.titleMeta = articleListMeta.titleMeta
            this.viewCountMeta = articleListMeta.viewCountMeta
        }.build()
        articleListItemExtractor.id = entry.key
        articleListItemExtractor.nextExtractorId = articleListMeta.nextExtractorId
        articleListItemExtractorMap[entry.key] = articleListItemExtractor
    }
    return articleListItemExtractorMap
}

fun getCommentListItemExtractorMap(meta: Map<String, CommentListMeta>?): Map<String, ListItemExtractor<Comment>> {
    val commentListItemExtractorMap = HashMap<String, ListItemExtractor<Comment>>()
    if (meta==null) return commentListItemExtractorMap
    for (entry in meta) {
        val commentListMeta = entry.value
        val commentListItemExtractor = CommentListItemExtractorBuilder().apply {
            this.contentMeta = commentListMeta.contentMeta
            this.dateMeta = commentListMeta.dateMeta
            this.listSelector = commentListMeta.listSelector
            this.nextPageMeta = commentListMeta.nextPageMeta
            this.repliedAvatarMeta = commentListMeta.repliedAvatarMeta
            this.repliedIdMeta = commentListMeta.repliedIdMeta
            this.repliedNameMeta = commentListMeta.repliedNameMeta
            this.scoreMeta = commentListMeta.scoreMeta
            this.userAvatarMeta = commentListMeta.userAvatarMeta
            this.userIdMeta = commentListMeta.userIdMeta
            this.userNameMeta = commentListMeta.userNameMeta
        }.build()
        commentListItemExtractor.id = entry.key
        commentListItemExtractor.nextExtractorId = commentListMeta.nextExtractorId
        commentListItemExtractorMap[entry.key] = commentListItemExtractor
    }
    return commentListItemExtractorMap
}

fun getCategoryExtractorMap(meta: Map<String, CategoryMeta>?): HashMap<String, CategoryExtractor> {
    val categoryExtractorMap = HashMap<String, CategoryExtractor>()
    if (meta==null) return categoryExtractorMap
    for (entry in meta) {
        val categoryMeta = entry.value
        val categoryExtractor = CategoryExtractor(
            nameMeta = categoryMeta.nameMeta,
            linkMeta = categoryMeta.linkMeta,
            iconMeta = categoryMeta.iconMeta,
            lastUpdateMeta = categoryMeta.lastUpdateMeta,
            countMeta = categoryMeta.countMeta
        )
        categoryExtractor.id = entry.key
        categoryExtractor.nextExtractorId = categoryMeta.nextExtractorId
        categoryExtractorMap[entry.key] = categoryExtractor
    }
    return categoryExtractorMap
}

fun getArticleExtractorMap(meta: Map<String, ArticleMeta>?): HashMap<String, ArticleExtractor> {
    val articleExtractorMap = HashMap<String, ArticleExtractor>()
    if (meta==null) return articleExtractorMap
    for (entry in meta) {
        val articleMeta = entry.value
        val articleExtractor = ArticleExtractor(
            titleMeta = articleMeta.titleMeta,
            dateMeta = articleMeta.dateMeta,
            coverMeta = articleMeta.coverMeta,
            abstractMeta = articleMeta.abstractMeta,
            scoreMeta = articleMeta.scoreMeta,
            contentMeta = articleMeta.contentMeta,
            viewCountMeta = articleMeta.viewCountMeta,
            nextPageMeta = articleMeta.nextPageMeta,
            authorIdMeta = articleMeta.authorIdMeta,
            authorNameMeta = articleMeta.authorNameMeta,
            authorAvatarMeta = articleMeta.authorAvatarMeta
        )
        articleExtractor.id = entry.key
        articleExtractor.nextExtractorId = articleMeta.nextExtractorId
        articleExtractorMap[entry.key] = articleExtractor
    }
    return articleExtractorMap
}

fun getGalleryExtractorMap(meta: Map<String, GalleryMeta>?): HashMap<String, GalleryExtractor> {
    val galleryExtractorMap = HashMap<String, GalleryExtractor>()
    if (meta==null) return galleryExtractorMap
    for (entry in meta) {
        val galleryMeta = entry.value
        val articleExtractor = ArticleExtractor(
            titleMeta = galleryMeta.titleMeta,
            dateMeta = galleryMeta.dateMeta,
            coverMeta = galleryMeta.coverMeta,
            abstractMeta = galleryMeta.abstractMeta,
            scoreMeta = galleryMeta.scoreMeta,
            contentMeta = galleryMeta.contentMeta,
            viewCountMeta = galleryMeta.viewCountMeta,
            nextPageMeta = galleryMeta.nextPageMeta,
            authorIdMeta = galleryMeta.authorIdMeta,
            authorNameMeta = galleryMeta.authorNameMeta,
            authorAvatarMeta = galleryMeta.authorAvatarMeta
        )
        val galleryExtractor = GalleryExtractor(
            imageMeta = galleryMeta.imageMeta,
            articleExtractor = articleExtractor
        )
        galleryExtractor.id = entry.key
        galleryExtractor.nextExtractorId = galleryMeta.nextExtractorId
        galleryExtractorMap[entry.key] = galleryExtractor
    }
    return galleryExtractorMap
}

fun getVideoExtractorMap(meta: Map<String, VideoMeta>?): HashMap<String, VideoExtractor> {
    val videoExtractorMap = HashMap<String, VideoExtractor>()
    if (meta==null) return videoExtractorMap
    for (entry in meta) {
        val videoMeta = entry.value
        val articleExtractor = ArticleExtractor(
            titleMeta = videoMeta.titleMeta,
            dateMeta = videoMeta.dateMeta,
            coverMeta = videoMeta.coverMeta,
            abstractMeta = videoMeta.abstractMeta,
            scoreMeta = videoMeta.scoreMeta,
            contentMeta = videoMeta.contentMeta,
            viewCountMeta = videoMeta.viewCountMeta,
            nextPageMeta = videoMeta.nextPageMeta,
            authorIdMeta = videoMeta.authorIdMeta,
            authorNameMeta = videoMeta.authorNameMeta,
            authorAvatarMeta = videoMeta.authorAvatarMeta
        )
        val videoExtractor = VideoExtractor(
            videoMeta = videoMeta.videoMeta,
            articleExtractor = articleExtractor
        )
        videoExtractor.id = entry.key
        videoExtractor.nextExtractorId = videoMeta.nextExtractorId
        videoExtractorMap[entry.key] = videoExtractor
    }
    return videoExtractorMap
}
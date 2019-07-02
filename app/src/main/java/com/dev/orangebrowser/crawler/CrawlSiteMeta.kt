package com.dev.orangebrowser.crawler

import com.dev.orangebrowser.crawler.model.ItemExtractorMeta


class CrawlSiteMeta(
    var categoryListMetaMap: Map<String, CategoryListMeta>?,
    var categoryMetaMap: Map<String, CategoryMeta>?,
    var articleListMetaMap: Map<String, ArticleListMeta>?,
    var articleMetaMap: Map<String, ArticleMeta>?,
    var commentListMetaMap: Map<String, CommentListMeta>?,
    var galleryMetaMap: Map<String, GalleryMeta>?,
    var videoMetaMap: Map<String, VideoMeta>?
)

open class BaseMeta(var id: String? = DEFAULT_EXTRACTOR_ID, var nextExtractorId: String? = DEFAULT_EXTRACTOR_ID)

class CategoryListMeta(
    var nameMeta: ItemExtractorMeta?,
    var linkMeta: ItemExtractorMeta?,
    var iconMeta: ItemExtractorMeta?,
    var lastUpdateMeta: ItemExtractorMeta?,
    var countMeta: ItemExtractorMeta?,
    var nextPageMeta: ItemExtractorMeta?,
    var listSelector: String?
) : BaseMeta()

class CategoryMeta(
    var nameMeta: ItemExtractorMeta?,
    var linkMeta: ItemExtractorMeta?,
    var iconMeta: ItemExtractorMeta?,
    var lastUpdateMeta: ItemExtractorMeta?,
    var countMeta: ItemExtractorMeta?
) : BaseMeta()

class ArticleListMeta(
    var titleMeta: ItemExtractorMeta?,
    var dateMeta: ItemExtractorMeta?,
    var coverMeta: ItemExtractorMeta?,
    var abstractMeta: ItemExtractorMeta?,
    var scoreMeta: ItemExtractorMeta?,
    var contentMeta: ItemExtractorMeta?,
    var viewCountMeta: ItemExtractorMeta?,
    var nextPageMeta: ItemExtractorMeta?,
    var listSelector: String?,
    var authorIdMeta: ItemExtractorMeta?,
    var authorNameMeta: ItemExtractorMeta?,
    var authorAvatarMeta: ItemExtractorMeta?
) : BaseMeta()

class ArticleMeta(
    var titleMeta: ItemExtractorMeta?,
    var dateMeta: ItemExtractorMeta?,
    var coverMeta: ItemExtractorMeta?,
    var abstractMeta: ItemExtractorMeta?,
    var scoreMeta: ItemExtractorMeta?,
    var contentMeta: ItemExtractorMeta?,
    var viewCountMeta: ItemExtractorMeta?,
    var nextPageMeta: ItemExtractorMeta?,
    var listSelector: String?,
    var authorIdMeta: ItemExtractorMeta?,
    var authorNameMeta: ItemExtractorMeta?,
    var authorAvatarMeta: ItemExtractorMeta?
) : BaseMeta()

class CommentListMeta(
    var dateMeta: ItemExtractorMeta?,
    var scoreMeta: ItemExtractorMeta?,
    var contentMeta: ItemExtractorMeta?,
    var nextPageMeta: ItemExtractorMeta?,
    var listSelector: String?,
    var userIdMeta: ItemExtractorMeta?,
    var userNameMeta: ItemExtractorMeta?,
    var userAvatarMeta: ItemExtractorMeta?,
    var repliedIdMeta: ItemExtractorMeta?,
    var repliedNameMeta: ItemExtractorMeta?,
    var repliedAvatarMeta: ItemExtractorMeta?
) : BaseMeta()

class GalleryMeta(
    var titleMeta: ItemExtractorMeta?,
    var dateMeta: ItemExtractorMeta?,
    var coverMeta: ItemExtractorMeta?,
    var abstractMeta: ItemExtractorMeta?,
    var scoreMeta: ItemExtractorMeta?,
    var contentMeta: ItemExtractorMeta?,
    var viewCountMeta: ItemExtractorMeta?,
    var nextPageMeta: ItemExtractorMeta?,
    var listSelector: String?,
    var authorIdMeta: ItemExtractorMeta?,
    var authorNameMeta: ItemExtractorMeta?,
    var authorAvatarMeta: ItemExtractorMeta?,
    var imageMeta: ItemExtractorMeta?
) : BaseMeta()

class VideoMeta(
    var titleMeta: ItemExtractorMeta?,
    var dateMeta: ItemExtractorMeta?,
    var coverMeta: ItemExtractorMeta?,
    var abstractMeta: ItemExtractorMeta?,
    var scoreMeta: ItemExtractorMeta?,
    var contentMeta: ItemExtractorMeta?,
    var viewCountMeta: ItemExtractorMeta?,
    var nextPageMeta: ItemExtractorMeta?,
    var listSelector: String?,
    var authorIdMeta: ItemExtractorMeta?,
    var authorNameMeta: ItemExtractorMeta?,
    var authorAvatarMeta: ItemExtractorMeta?,
    var videoMeta: ItemExtractorMeta?
) : BaseMeta()
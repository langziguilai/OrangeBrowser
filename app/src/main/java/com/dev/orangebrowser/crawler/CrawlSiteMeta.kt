package com.dev.orangebrowser.crawler

import com.dev.orangebrowser.crawler.model.ItemExtractorMeta


class CrawlSiteMeta(
    var categoryListMeta: CategoryListMeta,
    var categoryMeta: CategoryMeta,
    var articleListMeta: ArticleListMeta,
    var articleMeta: ArticleMeta,
    var commentListMeta: CommentListMeta,
    var galleryMeta: GalleryMeta,
    var videoMeta: VideoMeta
)

class CategoryListMeta(
    var nameMeta: ItemExtractorMeta?,
    var linkMeta: ItemExtractorMeta?,
    var iconMeta: ItemExtractorMeta?,
    var lastUpdateMeta: ItemExtractorMeta?,
    var countMeta: ItemExtractorMeta?,
    var nextPageMeta: ItemExtractorMeta?,
    var listSelector: String?
)

class CategoryMeta(
    var nameMeta: ItemExtractorMeta?,
    var linkMeta: ItemExtractorMeta?,
    var iconMeta: ItemExtractorMeta?,
    var lastUpdateMeta: ItemExtractorMeta?,
    var countMeta: ItemExtractorMeta?
)

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
)

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
)

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
)

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
)
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
)
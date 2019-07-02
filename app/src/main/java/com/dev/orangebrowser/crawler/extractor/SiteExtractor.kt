package com.dev.orangebrowser.crawler.extractor

import com.dev.orangebrowser.crawler.DEFAULT_EXTRACTOR_ID
import com.dev.orangebrowser.crawler.EMPTY
import com.dev.orangebrowser.crawler.model.*
import org.jsoup.nodes.Element

class SiteExtractor(
    var categoryListItemExtractorMap: Map<String, ListItemExtractor<Category>>,
    var articleListItemExtractorMap: Map<String, ListItemExtractor<Article>>,
    var commentListItemExtractorMap: Map<String, ListItemExtractor<Comment>>,
    var articleItemExtractorMap: Map<String, ArticleExtractor>,
    var galleryItemExtractorMap: Map<String, GalleryExtractor>,
    var videoItemExtractorMap: Map<String, VideoExtractor>
) {
    //获取category列表
    fun getCategoriyList(element: Element, extractorId: String = DEFAULT_EXTRACTOR_ID): List<Category> {
        var result = listOf<Category>()
        categoryListItemExtractorMap[extractorId]?.apply {
            result = this.extractList(element)
        }
        return result
    }

    //获取文章列表
    fun getAritcleList(element: Element, extractorId: String = DEFAULT_EXTRACTOR_ID): List<Article> {
        var result = listOf<Article>()
        articleListItemExtractorMap[extractorId]?.apply {
            result = this.extractList(element)
        }
        return result
    }

    //获取文章列表的下一页的链接
    fun getNextPageAritcleListUrl(element: Element, extractorId: String = DEFAULT_EXTRACTOR_ID): String {
        var result = EMPTY
        articleListItemExtractorMap[extractorId]?.apply {
            result = this.extractNextPageUrl(element)
        }
        return result
    }

    //获取评论列表
    fun getCommentList(element: Element, extractorId: String = DEFAULT_EXTRACTOR_ID): List<Comment> {
        var result = listOf<Comment>()
        commentListItemExtractorMap[extractorId]?.apply {
            result = this.extractList(element)
        }
        return result
    }

    //获取文章列表的下一页的链接
    fun getNextPageCommentListUrl(element: Element, extractorId: String = DEFAULT_EXTRACTOR_ID): String {
        var result = EMPTY
        commentListItemExtractorMap[extractorId]?.apply {
            result = this.extractNextPageUrl(element)
        }
        return result
    }

    //获取文章
    fun getAritcle(element: Element, extractorId: String = DEFAULT_EXTRACTOR_ID, oldArticle: Article) {
        articleItemExtractorMap[extractorId]?.apply {
            this.extract(element, oldArticle)
        }
    }

    //获取文章下一部分的链接
    fun getAritcleNextPageUrl(element: Element, extractorId: String = DEFAULT_EXTRACTOR_ID): String {
        var result = EMPTY
        articleItemExtractorMap[extractorId]?.apply {
            result = this.extractNextPageUrl(element)
        }
        return result
    }

    //获取视频
    fun getVideo(element: Element, extractorId: String = DEFAULT_EXTRACTOR_ID, oldVideo: Video) {
        videoItemExtractorMap[extractorId]?.apply {
            this.extract(element, oldVideo)
        }
    }

    //获取视频类文章的下一部分的链接
    fun getVideoNextPageUrl(element: Element, extractorId: String = DEFAULT_EXTRACTOR_ID): String {
        var result = EMPTY
        videoItemExtractorMap[extractorId]?.apply {
            result = this.extractNextPageUrl(element)
        }
        return result
    }

    //获取Gallery
    fun getGallery(element: Element, extractorId: String = DEFAULT_EXTRACTOR_ID, oldGallery: Gallery) {
        galleryItemExtractorMap[extractorId]?.apply {
            this.extract(element, oldGallery)
        }
    }

    //获取Gallery类文章的下一部分的链接
    fun getGalleryNextPageUrl(element: Element, extractorId: String = DEFAULT_EXTRACTOR_ID): String {
        var result = EMPTY
        galleryItemExtractorMap[extractorId]?.apply {
            result = this.extractNextPageUrl(element)
        }
        return result
    }
}
package com.dev.orangebrowser.crawler.extractor.builder

import com.dev.orangebrowser.crawler.extractor.*
import com.dev.orangebrowser.crawler.model.*
import kotlin.collections.HashMap

class SiteExtractorBuilder {
    //category列表解析器
    private var categoryListItemExtractorMap:HashMap<String,ListItemExtractor<Category>> = HashMap()
    fun addCategoryListItemExtractor(extractor:ListItemExtractor<Category>){
        categoryListItemExtractorMap[extractor.id] = extractor
    }
    //article列表解析器
    private var articleListItemExtractorMap:HashMap<String,ListItemExtractor<Article>> = HashMap()
    fun addArticleListItemExtractor(extractor: ListItemExtractor<Article>){
        articleListItemExtractorMap[extractor.id] = extractor
    }
    //article列表解析器
    private var commentListItemExtractorMap:HashMap<String,ListItemExtractor<Comment>> = HashMap()
    fun addCommentListItemExtractor(extractor: ListItemExtractor<Comment>){
        commentListItemExtractorMap[extractor.id] = extractor
    }
    //article解析器
    private var articleItemExtractorMap:HashMap<String,ArticleExtractor> = HashMap()
    fun addArticleItemExtractor(extractor:ArticleExtractor){
        articleItemExtractorMap[extractor.id]=extractor
    }
    //gallery解析器
    private var galleryItemExtractorMap:HashMap<String,GalleryExtractor> = HashMap()
    fun addGalleryItemExtractor(extractor: GalleryExtractor){
        galleryItemExtractorMap[extractor.id] = extractor
    }
    //vidoe解析器
    private var videoItemExtractorMap:HashMap<String,VideoExtractor> = HashMap()
    fun addVideoItemExtractor(extractor:VideoExtractor){
        videoItemExtractorMap[extractor.id] = extractor
    }
    fun build():SiteExtractor{
        return SiteExtractor(
            categoryListItemExtractorMap=categoryListItemExtractorMap,
            articleListItemExtractorMap = articleListItemExtractorMap,
            commentListItemExtractorMap = commentListItemExtractorMap,
            articleItemExtractorMap = articleItemExtractorMap,
            galleryItemExtractorMap = galleryItemExtractorMap,
            videoItemExtractorMap = videoItemExtractorMap
        )
    }
}
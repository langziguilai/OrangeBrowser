package com.dev.orangebrowser.crawler.extractor.builder

import com.dev.orangebrowser.crawler.extractor.ListItemExtractor
import com.dev.orangebrowser.crawler.model.Comment
import com.dev.orangebrowser.crawler.model.ItemExtractorMeta
import com.dev.orangebrowser.crawler.model.REPLY_COMMENT_TYPE
import com.dev.orangebrowser.crawler.model.User

class CommentListItemExtractorBuilder {
    private var dateMeta: ItemExtractorMeta? = null
    private var scoreMeta: ItemExtractorMeta? = null
    private var contentMeta: ItemExtractorMeta? = null
    private lateinit var nextPageMeta: ItemExtractorMeta
    private lateinit var listSelector: String
    private var userIdMeta: ItemExtractorMeta? = null
    private var userNameMeta: ItemExtractorMeta? = null
    private var userAvatarMeta: ItemExtractorMeta? = null
    private var repliedIdMeta: ItemExtractorMeta? = null
    private var repliedNameMeta: ItemExtractorMeta? = null
    private var repliedAvatarMeta: ItemExtractorMeta? = null
    fun load(source: String) {

    }

    fun build(): ListItemExtractor<Comment> {
        val listItemMetaMap = HashMap<String, ItemExtractorMeta?>()
        listItemMetaMap[DATE] = dateMeta
        listItemMetaMap[SCORE] = scoreMeta
        listItemMetaMap[CONTENT] = contentMeta
        listItemMetaMap[USER_ID] = userIdMeta
        listItemMetaMap[USER_NAME] = userNameMeta
        listItemMetaMap[USER_AVATAR] = userAvatarMeta
        listItemMetaMap[REPLIED_ID] = repliedIdMeta
        listItemMetaMap[REPLIED_NAME] = repliedNameMeta
        listItemMetaMap[REPLIED_AVATAR] = repliedAvatarMeta
        val mapper = fun(map: Map<String, String>): Comment {
            val comment = Comment()
            map[DATE]?.apply {
                comment.date = this
            }
            map[CONTENT]?.apply {
                comment.content = this
            }
            map[SCORE]?.apply {
                comment.score = this
            }
            val user = User()
            map[USER_ID]?.apply {
                if (this.isNotBlank()) user.id = this
            }
            map[USER_NAME]?.apply {
                if (this.isNotBlank()) user.username = this
            }
            map[USER_AVATAR]?.apply {
                if (this.isNotBlank()) user.avatar = this
            }
            comment.user = user
            val repliedUser = User()
            map[REPLIED_ID]?.apply {
                if (this.isNotBlank()) repliedUser.id = this
            }
            map[REPLIED_NAME]?.apply {
                if (this.isNotBlank()) repliedUser.username = this
            }
            map[REPLIED_AVATAR]?.apply {
                if (this.isNotBlank()) repliedUser.avatar = this
            }
            comment.repliedUser = repliedUser
            if (repliedUser.id.isNotBlank()){
                comment.type=REPLY_COMMENT_TYPE
            }
            return comment
        }
        return ListItemExtractor(
            listSelector = listSelector,
            nextPageMeta = nextPageMeta,
            listItemMetaMap = listItemMetaMap,
            mapper = mapper
        )
    }

    companion object {
        const val DATE = "date"
        const val SCORE = "score"
        const val CONTENT = "content"
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_AVATAR = "user_avatar"
        const val REPLIED_ID = "replied_id"
        const val REPLIED_NAME = "replied_name"
        const val REPLIED_AVATAR = "replied_avatar"
    }
}
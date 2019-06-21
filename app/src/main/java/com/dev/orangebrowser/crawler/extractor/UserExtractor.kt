package com.dev.orangebrowser.crawler.extractor

import com.dev.orangebrowser.crawler.model.ItemExtractorMeta
import com.dev.orangebrowser.crawler.model.User
import org.jsoup.nodes.Element

class UserExtractor(
    var userIdMeta: ItemExtractorMeta?=null,
    var userNameMeta: ItemExtractorMeta?=null,
    var userAvatarMeta: ItemExtractorMeta?=null
):BaseExtractor(){
    fun extract(element: Element): User {
        val user=User()
        extractItem(element,userIdMeta).apply {
            if (this.isNotBlank()) user.id=this
        }
        extractItem(element,userNameMeta).apply {
            if (this.isNotBlank()) user.username=this
        }
        extractItem(element,userAvatarMeta).apply {
            if (this.isNotBlank()) user.avatar=this
        }
        return user
    }
    companion object{
        fun build(userIdMeta: ItemExtractorMeta?=null,
                  userNameMeta: ItemExtractorMeta?=null,
                  userAvatarMeta: ItemExtractorMeta?=null):UserExtractor{
            return UserExtractor(userIdMeta,userNameMeta,userAvatarMeta)
        }
    }
}
package com.dev.orangebrowser.bloc.imageMode

import androidx.lifecycle.MutableLiveData
import com.dev.base.CoroutineViewModel
import com.dev.orangebrowser.config.ErrorCode
import com.dev.orangebrowser.utils.html2article.ContentExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class ImageModeViewModel @Inject constructor() : CoroutineViewModel() {
    var refreshImagesLiveData = MutableLiveData<List<String>>()
    var loadMoreImagesLiveData = MutableLiveData<List<String>>()
    var nextPageUrlLiveData = MutableLiveData<String>()
    var htmlLiveData = MutableLiveData<String>()
    var errCodeLiveData=MutableLiveData<Int>()
    fun refresh(
        url: String,
        showAllImages: Boolean = false,
        imageAttr: String = "abs:src",
        nextPageSelector: String = "",
        headers: Map<String, String>? = HashMap<String, String>().apply {
            put(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Mobile Safari/537.36"
            )
        }
    ) =launch(Dispatchers.IO){
        try {
            //下载
            var connection = Jsoup.connect(url)
            headers?.apply {
                connection = connection.headers(headers)
            }
            connection.timeout(20000)
            val documentOriginal = connection.get()
            val html=documentOriginal.html()
            htmlLiveData.postValue(html)
            if (nextPageSelector.isNotBlank()){
                documentOriginal.select(nextPageSelector).apply {
                    if(this.size>0){
                        nextPageUrlLiveData.postValue(this[0].attr("abs:href"))
                    }
                }
            }
            val document = if (!showAllImages) {
                val article = ContentExtractor.getArticleByHtml(html)
                Jsoup.parse(article.contentHtml)
            } else {
                Jsoup.parse(html)
            }
            document.setBaseUri(url)
            val newImages = extractImage(document,imageAttr)
            refreshImagesLiveData.postValue(newImages)
        }catch (e:Exception){
            e.printStackTrace()
            errCodeLiveData.postValue(ErrorCode.LOAD_FAIL)
        }
    }
    private fun extractImage(document: Document,imageAttr: String="abs:src"): List<String> {
        val list = LinkedList<String>()
        val elements = document.select("img")
        for (ele in elements) {
            val imageSrc = if (imageAttr.startsWith("abs:")){
                ele.attr(imageAttr).trim()
            }else{
                ele.attr("abs:$imageAttr").trim()
            }
            //如果不是直接设置数据的，就添加
            if (!imageSrc.startsWith("data:") && imageSrc.isNotBlank()) {
                list.add(imageSrc)
            }
        }
        return list
    }

    fun loadMore(
        url: String,
        showAllImages: Boolean = false,
        imageAttr: String = "",
        nextPageSelector: String = "",
        headers: Map<String, String>? = HashMap<String, String>().apply {
            put(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Mobile Safari/537.36"
            )
        }
    ) =launch(Dispatchers.IO){
        try {
            //下载
            var connection = Jsoup.connect(url)
            headers?.apply {
                connection = connection.headers(headers)
            }
            connection.timeout(20000)
            val documentOriginal = connection.get()
            val html=documentOriginal.html()
            htmlLiveData.postValue(html)
            documentOriginal.select(nextPageSelector).apply {
                if(this.size>0){
                    nextPageUrlLiveData.postValue(this[0].attr("abs:href"))
                }
            }
            val document = if (!showAllImages) {
                val article = ContentExtractor.getArticleByHtml(html)
                Jsoup.parse(article.contentHtml)
            } else {
                Jsoup.parse(html)
            }
            document.setBaseUri(url)
            val newImages = extractImage(document,imageAttr)
            loadMoreImagesLiveData.postValue(newImages)
        }catch (e:Exception){
            e.printStackTrace()
            errCodeLiveData.postValue(ErrorCode.LOAD_FAIL)
        }
    }
}

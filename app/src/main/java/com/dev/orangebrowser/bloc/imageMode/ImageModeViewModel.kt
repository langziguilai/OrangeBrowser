package com.dev.orangebrowser.bloc.imageMode

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.dev.base.CoroutineViewModel
import com.dev.orangebrowser.config.ResultCode
import com.dev.orangebrowser.data.dao.ImageModeMetaDao
import com.dev.orangebrowser.data.model.ImageModeMeta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class ImageModeViewModel @Inject constructor() : CoroutineViewModel() {
    @Inject
    lateinit var imageModeMetaDao: ImageModeMetaDao
    var refreshImagesLiveData = MutableLiveData<List<ImageInfo>>()
    var loadMoreImagesLiveData = MutableLiveData<List<ImageInfo>>()
    var nextPageUrlLiveData = MutableLiveData<String>()
    var htmlLiveData = MutableLiveData<String>()
    var resultCodeLiveData=MutableLiveData<Int>()
    var imageModeMetasLiveData = MutableLiveData<List<ImageModeMeta>>()
    var documentLiveData = MutableLiveData<Document>()
    fun upSertImageModeMeta(imageSiteMeta:ImageModeMeta)=launch(Dispatchers.IO){
        try {
            val existMeta= imageModeMetaDao.getByUniqueKey(imageSiteMeta.uniqueKey)
            if (existMeta!=null){
                existMeta.site=imageSiteMeta.site
                existMeta.imageAttr=imageSiteMeta.imageAttr
                existMeta.imageAttrTitle=imageSiteMeta.imageAttrTitle
                existMeta.nextPageSelector=imageSiteMeta.nextPageSelector
                existMeta.nextPageSelectorTitle=imageSiteMeta.nextPageSelectorTitle
                existMeta.replaceNthChildWithLastChild=imageSiteMeta.replaceNthChildWithLastChild
                existMeta.uniqueKey=imageSiteMeta.uniqueKey
                existMeta.contentSelector=imageSiteMeta.contentSelector
                imageModeMetaDao.update(existMeta)
            }else{
                imageModeMetaDao.insertAll(imageSiteMeta)
            }
            resultCodeLiveData.postValue(ResultCode.SAVE_SUCCESS)
        }catch (e:Exception){
            e.printStackTrace()
            resultCodeLiveData.postValue(ResultCode.SAVE_FAIL)
        }
    }
    fun deleteImageModeMeta(uid:Int)=launch(Dispatchers.IO){
        if (uid>=0){
            try {
                imageModeMetaDao.delete(uid = uid)
                resultCodeLiveData.postValue(ResultCode.DELETE_SUCCESS)
            }catch (e:Exception){
                e.printStackTrace()
                resultCodeLiveData.postValue(ResultCode.DELETE_FAIL)
            }
        }
    }
    fun loadImageModeMetas(url:String)=launch(Dispatchers.IO){
        //加载ImageModeMeta
        val host=Uri.parse(url).host ?: ""
        imageModeMetasLiveData.postValue(imageModeMetaDao.get(host))
    }
    fun refresh(
        url: String,
        contentSelector: String = "",
        imageAttr: String = "abs:src",
        nextPageSelector: String = "",
        headers: Map<String, String>? = HashMap<String, String>().apply {
            put(
                "User-Agent",
                "Mozilla/5.0 (Linux; Android 8.0; Pixel 2 Build/OPD3.170816.012) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Mobile Safari/537.36"
            )
        }
    ) =launch(Dispatchers.IO){
        //下载首页
        try {
            //下载
            var connection = Jsoup.connect(url)
            headers?.apply {
                connection = connection.headers(headers)
            }
            connection.timeout(20000)
            val documentOriginal = connection.get()
            documentLiveData.postValue(documentOriginal)
            val html=documentOriginal.html()
            htmlLiveData.postValue(html)
            if (nextPageSelector.isNotBlank()){
                documentOriginal.select(nextPageSelector).apply {
                    if(this.size>0){
                        nextPageUrlLiveData.postValue(this[0].attr("abs:href"))
                    }
                }
            }
            val document=Jsoup.parse(html)
            document.setBaseUri(url)
            val contentElement:Element? = if (contentSelector.isNotBlank()) {
                Jsoup.parse(html).select(contentSelector).first()
            } else {
                document
            }
            val newImages = extractImage(contentElement,imageAttr)
            refreshImagesLiveData.postValue(newImages)
            resultCodeLiveData.postValue(ResultCode.LOAD_SUCCESS)
        }catch (e:Exception){
            e.printStackTrace()
            resultCodeLiveData.postValue(ResultCode.LOAD_FAIL)
        }
    }
    private fun extractImage(element: Element?, imageAttr: String="abs:src"): List<ImageInfo> {
        val list = LinkedList<ImageInfo>()
        if (element==null) return list
        val elements = element.select("img")
        for (ele in elements) {
            val imageSrc = if (imageAttr.startsWith("abs:")){
                ele.attr(imageAttr).trim()
            }else{
                ele.attr("abs:$imageAttr").trim()
            }
            //如果不是直接设置数据的，就添加
            if (!imageSrc.startsWith("data:") && imageSrc.isNotBlank()) {
                val info=ImageInfo(url = imageSrc,selector = ele.cssSelector())
                list.add(info)
            }
        }
        return list
    }

    fun loadMore(
        url: String,
        contentSelector: String = "",
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
            val document=Jsoup.parse(html)
            document.setBaseUri(url)
            val contentElement:Element? = if (contentSelector.isNotBlank()) {
                Jsoup.parse(html).select(contentSelector).first()
            } else {
                document
            }
            val newImages = extractImage(contentElement,imageAttr)
            loadMoreImagesLiveData.postValue(newImages)
        }catch (e:Exception){
            e.printStackTrace()
            resultCodeLiveData.postValue(ResultCode.LOAD_FAIL)
        }
    }
}

data class ImageInfo(var url:String,var selector:String)
package com.dev.orangebrowser.bloc.browser.integration


import android.webkit.ValueCallback
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.concept.EngineSession
import com.dev.browser.concept.InterceptResource
import com.dev.browser.concept.InterceptResourceListener
import com.dev.browser.concept.MediaInterceptResource
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.resource.VideoResource
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.util.StringUtil
import org.jsoup.Jsoup
import java.util.*

//监听资源加载
class MediaResourceDetectIntegration(
    var binding: FragmentBrowserBinding,
    var fragment: BrowserFragment,
    var sessionManager: SessionManager,
    var session: Session
) :
    LifecycleAwareFeature {
    private var sessionObserver: Session.Observer
    private var  mediaResourceListener:InterceptResourceListener
    private var engineSession:EngineSession
    private  var videoResources: LinkedList<VideoResource> = LinkedList()
    private var videoResourcesSet=HashSet<String>()
    init {
        sessionObserver = object : Session.Observer {
            //在网页加载完成时，主动
            override fun onProgress(session: Session, progress: Int) {
                if(progress>=100){
                    sessionManager.getOrCreateEngineSession(session!!).executeJsFunction("javascript:getHtml();",
                        ValueCallback<String> { value ->
                            val html = StringUtil.unEscapeString(value)
                            val doc= Jsoup.parse(html)
                            val videoResourceTmp= doc.select("video").map {
                                val poster=it.attr("abs:poster")
                                val src=it.attr("src")
                                if(src.startsWith("//")){
                                    it.attr("src", "http:$src")
                                }
                                var link=it.attr("abs:src").trim()
                                if (link.isBlank()){
                                    val sources= it.select("source").map { source->source.attr("abs:src").trim() }
                                    for (source in sources){
                                        if (source.isNotBlank() && !source.startsWith("blob:")){
                                            link=source
                                            break
                                        }
                                    }
                                }
                                VideoResource(link=link,poster = poster,referer = session?.url ?: "")
                            }.filter { it.link.isNotBlank() }
                            for(videoResource in videoResourceTmp){
                                if(!videoResourcesSet.contains(videoResource.link)){
                                    videoResources.add(videoResource)
                                    videoResourcesSet.add(videoResource.link)
                                }
                            }
                            if (videoResources.size>0){
                                showPlayButton()
                            }
                    })

                }
            }
        }
        mediaResourceListener =object:InterceptResourceListener{
            override fun onResourceDeteceted(resource: InterceptResource) {
                if (resource is MediaInterceptResource){
                    if(!videoResourcesSet.contains(resource.link)){
                        videoResources.add(VideoResource(link = resource.link,referer = resource.referer))
                        videoResourcesSet.add(resource.link)
                    }
                    showPlayButton()
                }
            }
        }
        engineSession=sessionManager.getOrCreateEngineSession(session)
    }

    private fun showPlayButton() {

    }

    override fun start() {
        session.register(sessionObserver)
        engineSession.addResourceDetectListener(mediaResourceListener)
    }

    override fun stop() {
        session.unregister(sessionObserver)
        engineSession.removeResourceDetectListener(mediaResourceListener)
    }
}

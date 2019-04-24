package com.dev.orangebrowser.bloc.browser.integration

import android.graphics.Bitmap
import android.util.Log
import com.dev.base.extension.capture
import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.bloc.browser.BrowserFragment
import com.dev.orangebrowser.bloc.home.HomeFragment
import com.dev.orangebrowser.bloc.host.MainActivity
import com.dev.orangebrowser.databinding.FragmentBrowserBinding
import com.dev.util.FileUtil
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.SoftReference
import kotlin.coroutines.CoroutineContext

//绑定EngineView到LifecycleOwner，防止内存泄漏
class SessionManagerListenerIntegration(
    var session: Session,
    activity: MainActivity?,
    var binding:FragmentBrowserBinding,
    var fragment:BrowserFragment,
    var sessionManager: SessionManager
) :
    LifecycleAwareFeature {



    private val listener =
        SessionManagerObserver(originalSession = session, activity = activity, sessionManager = sessionManager,binding = binding,fragment = fragment)

    override fun start() {
        sessionManager.register(listener)
    }

    override fun stop() {
        sessionManager.unregister(listener)
    }
}

class SessionManagerObserver(
    var originalSession: Session,
    var activity: MainActivity?,
    var binding:FragmentBrowserBinding,
    var fragment:BrowserFragment,
    var sessionManager: SessionManager
) : SessionManager.Observer,CoroutineScope {
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job
    //添加的时候自动select，然后select的时候跳转
    override fun onSessionSelected(session: Session) {
        if (session.id!=originalSession.id){
            //再跳转其他session的时候，保存原始session的截图
            binding.webViewContainer.capture()?.apply {
                val bitmap = this
                originalSession.tmpThumbnail = SoftReference(bitmap)
                launch(Dispatchers.IO) {
                    try {
                        //TODO:压缩
                        val fileName = "${originalSession.id}.webp"
                        val file =
                            File(FileUtil.getOrCreateDir(fragment.requireContext(), Session.THUMBNAIL_DIR), fileName)
                        bitmap.compress(Bitmap.CompressFormat.WEBP, 80, FileOutputStream(file))
                        originalSession.thumbnailPath = File.separator + Session.THUMBNAIL_DIR + File.separator + fileName
                    } catch (e: Exception) {
                        Log.e("save thumbnail fail", e.message)
                    } finally {
                        coroutineContext.cancelChildren()
                    }
                }
            }
            activity?.loadBrowserFragment(session.id)
        }
    }

    //删除的时候，如果时删除的当前的OriginalSession，那么返回HomeFragment
    override fun onSessionRemoved(session: Session) {
        //删除本次的session
        if (originalSession.id == session.id) {
            activity?.loadHomeFragment(HomeFragment.NO_SESSION_ID)
        }
    }
}
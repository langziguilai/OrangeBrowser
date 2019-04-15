package com.dev.orangebrowser.bloc.browser.integration

import com.dev.base.support.LifecycleAwareFeature
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import com.dev.orangebrowser.bloc.home.HomeFragment
import com.dev.orangebrowser.bloc.host.MainActivity

//绑定EngineView到LifecycleOwner，防止内存泄漏
class SessionManagerListenerIntegration(
    var session: Session,
    activity: MainActivity?,
    var sessionManager: SessionManager
) :
    LifecycleAwareFeature {
    private val listener =
        SessionManagerObserver(originalSession = session, activity = activity, sessionManager = sessionManager)

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
    var sessionManager: SessionManager
) : SessionManager.Observer {
    //添加的时候自动select，然后select的时候跳转
    override fun onSessionSelected(session: Session) {
        if (session.id!=originalSession.id){
            //TODO:保存状态
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
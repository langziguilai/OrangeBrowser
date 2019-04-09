package com.dev.orangebrowser.browser

import android.content.Context
import javax.inject.Inject


class SessionManager @Inject constructor(val context:Context){
    private var currentIndex=-1
    //保存sessions
    private val sessions = mutableListOf<Session>()
    //获取的sessions的数量
    val size: Int
        get() = synchronized(sessions) { sessions.size }

    //保存session
    fun save(){

    }
    //恢复session
    fun restore(){

    }
    //获取当前session
    fun currentSessionOrNewOne():Session{
        synchronized(sessions){
            if(currentIndex<0){
                val session= Session.DefaultSession(context)
                sessions.add(session)
                currentIndex=sessions.size-1
                return session
            }
            return sessions[currentIndex]
        }
    }

}
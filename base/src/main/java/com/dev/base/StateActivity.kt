package com.dev.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dev.util.Keep
import com.evernote.android.state.StateSaver

//保存和恢复状态的Activity:https://github.com/evernote/android-state
@Keep
open class StateActivity:AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateSaver.restoreInstanceState(this, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState)
    }
}
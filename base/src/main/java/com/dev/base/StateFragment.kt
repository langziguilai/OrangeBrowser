package com.dev.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.dev.util.Keep
import com.evernote.android.state.StateSaver

//保存和恢复状态的Fragment:https://github.com/evernote/android-state
@Keep
open class StateFragment: Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateSaver.restoreInstanceState(this, savedInstanceState)
    }

        override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState)
    }
}

package com.dev.orangebrowser.view

import android.view.View


interface OnTriggerView {
    fun initial(data:Any)
    fun onTrigger()
    fun clear()
    fun asView():View
}
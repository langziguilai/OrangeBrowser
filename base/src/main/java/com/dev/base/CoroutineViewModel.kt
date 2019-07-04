package com.dev.base

import androidx.lifecycle.ViewModel
import com.dev.util.Keep
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
@Keep
open class CoroutineViewModel:ViewModel(),CoroutineScope{
    private val job= SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main+job

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancelChildren()
    }
}
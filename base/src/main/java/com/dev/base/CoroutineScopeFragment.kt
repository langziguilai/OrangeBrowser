package com.dev.base

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlin.coroutines.CoroutineContext

abstract class CoroutineScopeFragment:StateFragment(),CoroutineScope{
    private val job= SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main+job

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }
}
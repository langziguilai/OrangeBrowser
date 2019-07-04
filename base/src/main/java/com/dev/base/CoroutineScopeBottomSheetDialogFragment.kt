package com.dev.base

import com.dev.util.Keep
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlin.coroutines.CoroutineContext
@Keep
abstract class CoroutineScopeBottomSheetDialogFragment:BottomSheetDialogFragment(),CoroutineScope{
    private val job= SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main+job

    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }
}
package com.dev.base.support

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.dev.util.Keep

/**
 * An interface for all entry points to feature components to implement in order to make them lifecycle aware.
 */
@Keep
interface LifecycleAwareFeature : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop()
}

package com.dev.base.support

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * An interface for all entry points to feature components to implement in order to make them lifecycle aware.
 */
interface LifecycleAwareFeature : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop()
}

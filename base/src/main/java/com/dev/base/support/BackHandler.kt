package com.dev.base.support

/**
 * Generic interface for fragments, features and other components that want to handle 'back' button presses.
 */
interface BackHandler {
    /**
     * Called when this [BackHandler] gets the option to handle the user pressing the back key.
     *
     * Returns true if this [BackHandler] consumed the event and no other components need to be notified.
     */
    fun onBackPressed(): Boolean
}
package com.dev.base.support

import android.app.Activity
import com.dev.util.Keep

/**
 * Interface for fragments that want to handle user interactions.
 */
@Keep
interface UserInteractionHandler {
    /**
     * In most cases, when the home button is pressed, we invoke this onResult to inform the app that the user
     * is going to leave the app.
     *
     * See also [Activity.onUserLeaveHint] for more details.
     */
    fun onHomePressed(): Boolean
}
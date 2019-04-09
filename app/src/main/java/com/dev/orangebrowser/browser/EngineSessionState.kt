package com.dev.orangebrowser.browser

import org.json.JSONObject

/**
 * The state of an [EngineSession]. An instance can be obtained from [EngineSession.saveState]. Creating a new
 * [EngineSession] and calling [EngineSession.restoreState] with the same state instance should restore the previous
 * session.
 */
interface EngineSessionState {
    /**
     * Create a JSON representation of this state that can be saved to disk.
     *
     * When reading JSON from disk [Engine.createSessionState] can be used to turn it back into an [EngineSessionState]
     * instance.
     */
    fun toJSON(): JSONObject
}
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.browser.session.storage

import androidx.annotation.VisibleForTesting
import com.dev.browser.concept.Engine
import com.dev.browser.session.Session
import com.dev.browser.session.SessionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

// Current version of the format used.
private const val VERSION = 1

/**
 * Helper to transform [SessionManager.Snapshot] instances to JSON and back.
 */
class SnapshotSerializer {
    fun toJSON(snapshot: SessionManager.Snapshot): String {
        val json = JSONObject()
        json.put(Keys.VERSION_KEY, VERSION)
        json.put(Keys.SELECTED_SESSION_INDEX_KEY, snapshot.selectedSessionIndex)

        val sessions = JSONArray()
        snapshot.sessions.forEachIndexed { index, sessionWithState ->
            val sessionJson = JSONObject()
            sessionJson.put(Keys.SESSION_KEY, serializeSession(sessionWithState.session))

            val engineSessionState = if (sessionWithState.engineSessionState != null) {
                sessionWithState.engineSessionState.toJSON()
            } else {
                sessionWithState.engineSession?.saveState()?.toJSON() ?: JSONObject()
            }

            sessionJson.put(Keys.ENGINE_SESSION_KEY, engineSessionState)

            sessions.put(index, sessionJson)
        }
        json.put(Keys.SESSION_STATE_TUPLES_KEY, sessions)

        return json.toString()
    }

    fun fromJSON(engine: Engine, json: String): SessionManager.Snapshot {
        val tuples: MutableList<SessionManager.Snapshot.Item> = mutableListOf()

        val jsonRoot = JSONObject(json)
        val selectedSessionIndex = jsonRoot.getInt(Keys.SELECTED_SESSION_INDEX_KEY)

        val sessionStateTuples = jsonRoot.getJSONArray(Keys.SESSION_STATE_TUPLES_KEY)
        for (i in 0 until sessionStateTuples.length()) {
            val sessionStateTupleJson = sessionStateTuples.getJSONObject(i)
            val session = deserializeSession(sessionStateTupleJson.getJSONObject(Keys.SESSION_KEY))
            val state = engine.createSessionState(sessionStateTupleJson.getJSONObject(Keys.ENGINE_SESSION_KEY))

            tuples.add(SessionManager.Snapshot.Item(session, engineSession = null, engineSessionState = state))
        }

        return SessionManager.Snapshot(
            sessions = tuples,
            selectedSessionIndex = selectedSessionIndex
        )
    }
}

@Throws(JSONException::class)
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun serializeSession(session: Session): JSONObject {
    return JSONObject().apply {
        put(Keys.SESSION_URL_KEY, session.url)
        put(Keys.SESSION_FORBID_IMAGE, session.forbidImageMode)
        put(Keys.SESSION_SCREEN_NUMBER, session.screenNumber)
        put(Keys.SESSION_WEB_PAGE_THUMBNAIL_PATH, session.webPageThumbnailPath)
        put(Keys.SESSION_MAIN_PAGE_THUMBNAIL_PATH, session.mainPageThumbnailPath)
        put(Keys.SESSION_SOURCE_KEY, session.source.name)
        put(Keys.SESSION_UUID_KEY, session.id)
        put(Keys.SESSION_PARENT_UUID_KEY, session.parentId ?: "")
        put(Keys.SESSION_TITLE, session.title)
    }
}

@Throws(JSONException::class)
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
internal fun deserializeSession(json: JSONObject): Session {
    val source = try {
        Session.Source.valueOf(json.getString(Keys.SESSION_SOURCE_KEY))
    } catch (e: IllegalArgumentException) {
        Session.Source.NONE
    }
    val session = Session(
        json.getString(Keys.SESSION_URL_KEY),
        // Currently, snapshot cannot contain private sessions.
        false,
        source,
        json.getString(Keys.SESSION_UUID_KEY)
    )
    session.parentId = json.getString(Keys.SESSION_PARENT_UUID_KEY).takeIf { it != "" }
    session.title = if (json.has(Keys.SESSION_TITLE)) json.getString(Keys.SESSION_TITLE) else ""
    session.forbidImageMode = if (json.has(Keys.SESSION_FORBID_IMAGE)) json.getBoolean(Keys.SESSION_FORBID_IMAGE) else false
    session.screenNumber = if (json.has(Keys.SESSION_SCREEN_NUMBER)) json.getInt(Keys.SESSION_SCREEN_NUMBER) else Session.HOME_SCREEN
    session.title = if (json.has(Keys.SESSION_TITLE)) json.getString(Keys.SESSION_TITLE) else ""
    session.webPageThumbnailPath = if (json.has(Keys.SESSION_WEB_PAGE_THUMBNAIL_PATH)) json.getString(Keys.SESSION_WEB_PAGE_THUMBNAIL_PATH) else ""
    session.mainPageThumbnailPath = if (json.has(Keys.SESSION_MAIN_PAGE_THUMBNAIL_PATH)) json.getString(Keys.SESSION_MAIN_PAGE_THUMBNAIL_PATH) else ""
    return session
}

private object Keys {
    const val SELECTED_SESSION_INDEX_KEY = "selectedSessionIndex"
    const val SESSION_STATE_TUPLES_KEY = "sessionStateTuples"
    const val SESSION_SOURCE_KEY = "source"
    const val SESSION_URL_KEY = "url"
    const val SESSION_UUID_KEY = "uuid"
    const val SESSION_PARENT_UUID_KEY = "parentUuid"
    const val SESSION_TITLE = "title"
    const val SESSION_FORBID_IMAGE="forbidImageMode"
    const val SESSION_SCREEN_NUMBER="screenNumber"
    const val SESSION_WEB_PAGE_THUMBNAIL_PATH="webPageThumbnailPath"
    const val SESSION_MAIN_PAGE_THUMBNAIL_PATH="mainPageThumbnailPath"
    const val SESSION_KEY = "session"
    const val ENGINE_SESSION_KEY = "engineSession"
    const val VERSION_KEY = "version"
}

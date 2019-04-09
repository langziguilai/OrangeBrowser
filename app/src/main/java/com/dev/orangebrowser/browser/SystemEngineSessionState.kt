package com.dev.orangebrowser.browser

import android.os.Bundle
import org.json.JSONObject

class SystemEngineSessionState(
    internal val bundle: Bundle?
) : EngineSessionState {
    override fun toJSON(): JSONObject {
        if (bundle == null) {
            return JSONObject()
        }

        return JSONObject().apply {
            bundle.keySet().forEach { key ->
                val value = bundle[key]

                if (shouldSerialize(value)) {
                    put(key, value)
                }
            }
        }
    }

    companion object {
        fun fromJSON(json: JSONObject): SystemEngineSessionState {
            return SystemEngineSessionState(json.toBundle())
        }
    }
}

private fun shouldSerialize(value: Any?): Boolean {
    // For now we only persist primitive types
    // https://github.com/mozilla-mobile/android-components/issues/279
    return when (value) {
        is Number -> true
        is Boolean -> true
        is String -> true
        else -> false
    }
}

@Suppress("ComplexMethod")
private fun JSONObject.toBundle(): Bundle {
    val bundle = Bundle()

    keys().forEach { key ->
        val value = get(key)
        when (value) {
            is Int -> bundle.putInt(key, value)
            is Double -> bundle.putDouble(key, value)
            is Long -> bundle.putLong(key, value)
            is Float -> bundle.putFloat(key, value)
            is Char -> bundle.putChar(key, value)
            is Short -> bundle.putShort(key, value)
            is Byte -> bundle.putByte(key, value)
            is String -> bundle.putString(key, value)
            is Boolean -> bundle.putBoolean(key, value)
        }
    }

    return bundle
}

package com.dev.base.extension

import android.content.res.AssetManager
import org.json.JSONObject

/**
 * Read a file from the "assets" and create a a JSONObject from its content.
 *
 * @param fileName The name of the asset to open.  This name can be
 *                 hierarchical.
 */
fun AssetManager.readJSONObject(fileName: String) = JSONObject(open(fileName).bufferedReader().use {
    it.readText()
})
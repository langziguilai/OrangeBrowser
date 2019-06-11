package com.dev.browser.extension

import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_TEXT
import android.content.pm.PackageManager
import android.os.Process
import androidx.annotation.VisibleForTesting
import androidx.core.content.ContextCompat
import com.dev.browser.R
import com.dev.browser.support.log.Log


/**
 * The (visible) version name of the application, as specified by the <manifest> tag's versionName
 * attribute. E.g. "2.0".
 */
val Context.appVersionName: String?
    get() {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        return packageInfo.versionName
    }

/**
 * Returns the handle to a system-level service by name.
 */
inline fun <reified T> Context.systemService(name: String): T {
    return getSystemService(name) as T
}

/**
 * Returns whether or not the operating system is under low memory conditions.
 */
fun Context.isOSOnLowMemory(): Boolean {
    val activityManager = systemService<ActivityManager>(Context.ACTIVITY_SERVICE)
    return ActivityManager.MemoryInfo().also { memoryInfo ->
        activityManager.getMemoryInfo(memoryInfo)
    }.lowMemory
}

/**
 * Returns if a list of permission have been granted, if all the permission have been granted
 * returns true otherwise false.
 */
fun Context.isPermissionGranted(vararg permission: String): Boolean {
    return permission.all { ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED }
}

/**
 *  Shares mContent via [ACTION_SEND] intent.
 *
 * @param text the data to be shared  [EXTRA_TEXT]
 * @param subject of the intent [EXTRA_TEXT]
 * @return true it is able to share false otherwise.
 */
fun Context.share(text: String, subject: String = getString(R.string.mozac_support_ktx_share_dialog_title)): Boolean {
    return try {
        val intent = Intent(ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(EXTRA_TEXT, text)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val shareIntent = Intent.createChooser(intent, getString(R.string.mozac_support_ktx_menu_share_with)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        startActivity(shareIntent)
        true
    } catch (e: ActivityNotFoundException) {
        Log.log(Log.Priority.WARN, message = "No activity to share to found", throwable = e, tag = "Reference-Browser")
        false
    }
}

@VisibleForTesting
internal var isMainProcess: Boolean? = null

/**
 *  Returns true if we are running in the main process false otherwise.
 */
fun Context.isMainProcess(): Boolean {
    if (isMainProcess != null) return isMainProcess as Boolean

    val pid = Process.myPid()
    val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    isMainProcess = (activityManager.runningAppProcesses?.any { processInfo ->
        (processInfo.pid == pid && processInfo.processName == packageName)
    }) ?: false

    return isMainProcess as Boolean
}

/**
 * Takes a function runs it only it if we are running in the main process, otherwise the function will not be executed.
 * @param [block] function to be executed in the main process.
 */
inline fun Context.runOnlyInMainProcess(block: () -> Unit) {
    if (isMainProcess()) {
        block()
    }
}

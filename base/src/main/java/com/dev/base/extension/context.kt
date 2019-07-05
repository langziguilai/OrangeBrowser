/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package com.dev.base.extension

import android.app.ActivityManager
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.util.Log
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.JsonArray
import androidx.core.content.ContextCompat.getSystemService
import android.net.Uri
import android.view.Gravity
import androidx.core.content.ContextCompat.startActivity
import com.dev.base.R
import com.dev.util.Keep
import com.dev.view.dialog.AlertDialogBuilder


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
@Keep
inline fun <reified T> Context.systemService(name: String): T {
    return getSystemService(name) as T
}

/**
 * Returns whether or not the operating system is under low memory conditions.
 */
@Keep
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
@Keep
fun Context.isPermissionGranted(vararg permission: String): Boolean {
    return permission.all {
        ContextCompat.checkSelfPermission(this, it) == PERMISSION_GRANTED
    }
}
@Keep
fun Context.showMessage(message:String){

}
@Keep
fun Context.getPreferenceKey(@StringRes resourceId: Int): String =
    resources.getString(resourceId)
@Keep
fun <T>Context.loadJsonObject(path:String,mClass:Class<T>):T?{
    var result:T?=null
    try {
        val stream=this.assets.open(path)
        result= Gson().fromJson<T>(InputStreamReader(stream), mClass)
    }catch (e:Exception){
        Log.e("loadJsonArray","loadJsonArray error ",e)
    }
    return result
}
@Keep
fun <T> Context.loadJsonArray(path: String, clazz: Class<T>): List<T> {
    val stream=this.assets.open(path)
    val lst = ArrayList<T>()
    try {
        val array = JsonParser().parse(InputStreamReader(stream)).asJsonArray
        for (elem in array) {
            lst.add(Gson().fromJson(elem, clazz))
        }
    } catch (e: Exception) {
        Log.e("loadJsonArray","loadJsonArray error ",e)
    }
    return lst
}
@Keep
fun Context.showToast(content:String){
    val toast=Toast.makeText(this,"",Toast.LENGTH_SHORT)
    toast.setText(content)
    toast.show()
}
@Keep
fun Context.shareText(title:String,text:String):Boolean{
    return try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, text)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val shareIntent = Intent.createChooser(intent, getString(R.string.share)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(shareIntent)
        true
    } catch (e: ActivityNotFoundException) {
        Log.d("share error","No activity to share to found")
        false
    }
}
@Keep
fun Context.shareLink(title:String,url:String):Boolean{
    return try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share)+":"+title)
            putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link)+"-"+title+":"+url)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }

        val shareIntent = Intent.createChooser(intent, getString(R.string.share)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(shareIntent)
        true
    } catch (e: ActivityNotFoundException) {
        Log.d("share error","No activity to share to found")
        false
    }
}
//拷贝文字
@Keep
fun Context.copyText(label:String,text:String){
    val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val mClipData = ClipData.newPlainText(label, text)
    cm.primaryClip = mClipData
}
//拷贝链接
@Keep
fun Context.copyLink(label:String,link:String){
    val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val mClipData = ClipData.newUri(contentResolver,label, Uri.parse(link))
    cm.primaryClip = mClipData
}

//保证只有一个Dialog显示
var redirectDialog: Dialog?=null
var redirectUrl:String=""
//通过链接跳转到可以接收的应用
@Keep
fun Context.redirectToAppAsk(url:String){
    Log.d("redirectToAppAsk",url)
    redirectUrl=url
    if (redirectDialog==null){
        redirectDialog= AlertDialogBuilder()
            .setTitle(title = getString(R.string.tip))
            .setContent(content = getString(R.string.tip_redirect_to_other_app))
            .setGravity(Gravity.BOTTOM)
            .setNegativeButtonText(text=getString(R.string.cancel))
            .setOnPositive(Runnable {
                try {
                    Log.d("app url is :",redirectUrl)
                    val intent = Intent(Intent.ACTION_VIEW,Uri.parse(redirectUrl)).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Log.d("share error","No activity to share to found")
                }
            })
            .setOnNegative(Runnable {
            })
            .setPositiveButtonText(text=getString(R.string.sure))
            .build(this)
    }
    if(!redirectDialog!!.isShowing){
        redirectDialog?.show()
    }
}
@Keep
fun Context.redirectToApp(url:String){
    try {
        Log.d("app url is :",redirectUrl)
        val intent = Intent(Intent.ACTION_VIEW,Uri.parse(redirectUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Log.d("share error","No activity to share to found")
    }
}
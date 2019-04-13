package com.dev.orangebrowser.bloc.host

import android.content.ContentResolver
import android.content.Context
import android.content.pm.ActivityInfo
import android.provider.Settings
import android.view.OrientationEventListener

class OrientationDetector(var maxAngle: Int, var contentResolver: ContentResolver, context: Context) :
    OrientationEventListener(context) {
    var orientationType: Int = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    var num90AngleRange: IntRange = (90 - maxAngle + 1)..(90 + maxAngle)
    var num180AngleRange: IntRange = (180 - maxAngle + 1)..(180 + maxAngle)
    var num270AngleRange: IntRange = (270 - maxAngle + 1)..(270 + maxAngle)
    override fun onOrientationChanged(orientation: Int) {
        //判断是否屏幕锁定
        val isOrientationEnable: Boolean = try {
            Settings.System.getInt(contentResolver, Settings.System.ACCELEROMETER_ROTATION) == 1
        } catch (e: Throwable) {
            false
        }
        //如果可以旋转屏幕
        if (isOrientationEnable) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return  //手机平放时，检测不到有效的角度
            }
            //只检测是否有四个角度的改变
            if (orientation > 360 - maxAngle || orientation < maxAngle) { //0度
                orientationType = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else if (orientation in num90AngleRange) { //90度
                orientationType = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
            } else if (orientation in num180AngleRange) { //180度

            } else if (orientation in num270AngleRange) { //270度
                orientationType = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                return
            }
        }
    }
}

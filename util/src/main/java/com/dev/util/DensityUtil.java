package com.dev.util;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

public class DensityUtil {
    private static float sNoncompatDensity;
    private static float sNoncompatScaledDensity;
    public static void setCustomDensity(Activity activity,final Application application){
        final DisplayMetrics appDisplayMetrics=application.getResources().getDisplayMetrics();
        if (sNoncompatDensity==0){
            sNoncompatDensity=appDisplayMetrics.density;
            sNoncompatScaledDensity=appDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if(newConfig!=null && newConfig.fontScale>0){
                        sNoncompatScaledDensity=application.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        final float targetDensity=appDisplayMetrics.widthPixels/360.0f;
        final float targetScaledDensity = targetDensity*(sNoncompatScaledDensity/sNoncompatDensity);
        final int targetDensityDpi=(int)(160*targetDensity);
        //设置application
        appDisplayMetrics.density=targetDensity;
        appDisplayMetrics.scaledDensity=targetScaledDensity;
        appDisplayMetrics.densityDpi=targetDensityDpi;
        //设置activity
        final DisplayMetrics activityDisplayMetricvs=activity.getResources().getDisplayMetrics();
        activityDisplayMetricvs.density=targetDensity;
        activityDisplayMetricvs.scaledDensity=targetScaledDensity;
        activityDisplayMetricvs.densityDpi=targetDensityDpi;
    }
    public static void setCustomDensity(final Context applicationContext){
        final DisplayMetrics appDisplayMetrics=applicationContext.getResources().getDisplayMetrics();
        if (sNoncompatDensity==0){
            sNoncompatDensity=appDisplayMetrics.density;
            sNoncompatScaledDensity=appDisplayMetrics.scaledDensity;
            applicationContext.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(Configuration newConfig) {
                    if(newConfig!=null && newConfig.fontScale>0){
                        sNoncompatScaledDensity=applicationContext.getResources().getDisplayMetrics().scaledDensity;
                    }
                }

                @Override
                public void onLowMemory() {

                }
            });
        }
        final float targetDensity=appDisplayMetrics.widthPixels/360.0f;
        final float targetScaledDensity = targetDensity*(sNoncompatScaledDensity/sNoncompatDensity);
        final int targetDensityDpi=(int)(160*targetDensity);
        //设置application
        appDisplayMetrics.density=targetDensity;
        appDisplayMetrics.scaledDensity=targetScaledDensity;
        appDisplayMetrics.densityDpi=targetDensityDpi;
    }
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

package com.dev.util;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;


@KeepNameIfNecessary
public class DensityUtil { ;
    private static float DesignWidth=360.0f;
    private static Activity activity;
    private static  Application application;
    private static float sNoncompatDensity;
    private static float sNoncompatScaledDensity;
    @KeepNameIfNecessary
    public static void init(Activity mActivity,Application mApplication){
        activity=mActivity;
        application=mApplication;
    }
    @KeepNameIfNecessary
    public static void clear(){
        activity=null;
        application=null;
    }
    @KeepNameIfNecessary
    public static void resetDensity(){
        if (activity==null || application==null) return;
        final DisplayMetrics appDisplayMetrics=application.getResources().getDisplayMetrics();
        //if (sNoncompatDensity==0){
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
        //}
        final float targetDensity=appDisplayMetrics.widthPixels/DesignWidth;
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
    @KeepNameIfNecessary
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    @KeepNameIfNecessary
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

package com.dev.util;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.res.Configuration;
import android.util.DisplayMetrics;



public class DensityUtil {
    private static float LANDSCAPE_DESIGN_WIDTH=640f;
    private static float PORTRAIT_DESIGN_WIDTH=360f;
    private static float DesignWidth=360.0f;
    private static Activity activity;
    private static  Application application;
    //应用初始化的时候，可以设置设计宽度
    public static void setDesignWidthByOrientation(boolean isProtrait){
        if (isProtrait){
            DesignWidth=PORTRAIT_DESIGN_WIDTH;
        }else{
            DesignWidth=LANDSCAPE_DESIGN_WIDTH;
        }
    }
    private static float sNoncompatDensity;
    private static float sNoncompatScaledDensity;
    public static void init(Activity mActivity,Application mApplication){
        activity=mActivity;
        application=mApplication;
    }
    public static void clear(){
        activity=null;
        application=null;
    }
    public static void resetDensity(){
        if (activity==null || application==null) return;
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
//    public static void resetDensity(final Application application){
//        final DisplayMetrics appDisplayMetrics=application.getResources().getDisplayMetrics();
//        if (sNoncompatDensity==0){
//            sNoncompatDensity=appDisplayMetrics.density;
//            sNoncompatScaledDensity=appDisplayMetrics.scaledDensity;
//            application.registerComponentCallbacks(new ComponentCallbacks() {
//                @Override
//                public void onConfigurationChanged(Configuration newConfig) {
//                    if(newConfig!=null && newConfig.fontScale>0){
//                        sNoncompatScaledDensity=application.getResources().getDisplayMetrics().scaledDensity;
//                    }
//                }
//
//                @Override
//                public void onLowMemory() {
//
//                }
//            });
//        }
//        final float targetDensity=appDisplayMetrics.widthPixels/DesignWidth;
//        final float targetScaledDensity = targetDensity*(sNoncompatScaledDensity/sNoncompatDensity);
//        final int targetDensityDpi=(int)(160*targetDensity);
//        //设置application
//        appDisplayMetrics.density=targetDensity;
//        appDisplayMetrics.scaledDensity=targetScaledDensity;
//        appDisplayMetrics.densityDpi=targetDensityDpi;
//    }
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
    public static int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

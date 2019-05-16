package com.dev.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import com.dev.util.ColorKitUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NavigationBarUtil {
    public static void setNavigationBarColor(Activity activity,int color){
        activity.getWindow().setNavigationBarColor(color);
    }
    public static void setNavigationBarColor(Window window,int color){
        window.setNavigationBarColor(color);
    }
}

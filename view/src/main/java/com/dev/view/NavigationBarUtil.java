package com.dev.view;

import android.app.Activity;
import android.view.Window;
import com.dev.util.Keep;
import com.dev.util.KeepMemberIfNecessary;
import com.dev.util.KeepNameIfNecessary;

@KeepNameIfNecessary
public class NavigationBarUtil {
    @KeepMemberIfNecessary
    public static void setNavigationBarColor(Activity activity,int color){
        activity.getWindow().setNavigationBarColor(color);
    }
    @KeepMemberIfNecessary
    public static void setNavigationBarColor(Window window,int color){
        window.setNavigationBarColor(color);
    }
}

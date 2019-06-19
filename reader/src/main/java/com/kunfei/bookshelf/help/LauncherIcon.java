package com.kunfei.bookshelf.help;

import android.content.ComponentName;
import android.content.pm.PackageManager;

import com.kunfei.bookshelf.ReaderApplication;
import com.kunfei.bookshelf.R;

/**
 * Created by GKF on 2018/2/27.
 * 更换图标
 */

public class LauncherIcon {
    private static PackageManager packageManager = ReaderApplication.getInstance().getPackageManager();
    private static ComponentName componentNameMain = new ComponentName(ReaderApplication.getInstance(), "com.kunfei.bookshelf.view.activity.WelcomeActivity");
    private static ComponentName componentNameBookMain = new ComponentName(ReaderApplication.getInstance(), "com.kunfei.bookshelf.view.activity.WelcomeBookActivity");

    public static void ChangeIcon(String icon) {

        if (icon.equals(ReaderApplication.getInstance().getString(R.string.icon_book))) {
            if (packageManager.getComponentEnabledSetting(componentNameBookMain) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                //启用
                packageManager.setComponentEnabledSetting(componentNameBookMain,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                //禁用
                packageManager.setComponentEnabledSetting(componentNameMain,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }
        } else {
            if (packageManager.getComponentEnabledSetting(componentNameMain) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                //启用
                packageManager.setComponentEnabledSetting(componentNameMain,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                //禁用
                packageManager.setComponentEnabledSetting(componentNameBookMain,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            }
        }
    }

    public static String getInUseIcon() {
        if (packageManager.getComponentEnabledSetting(componentNameBookMain) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            return ReaderApplication.getInstance().getString(R.string.icon_book);
        }
        return ReaderApplication.getInstance().getString(R.string.icon_main);
    }
}

package com.dev.orangebrowser.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * <pre>
 *     author: Fan
 *     time  : 2018/1/7 下午4:56
 *     desc  : android安装应用(适用于各个版本)
 * </pre>
 */

public class InstallAppInstance {
    private Activity mAct;
    private String mPath;//下载下来后文件的路径
    public static int UNKNOWN_CODE = 2018;

    public InstallAppInstance(Activity mAct, String mPath) {
        this.mAct = mAct;
        this.mPath = mPath;
    }

    public void install(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startInstallO();
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) startInstallN();
        else startInstall();
    }

    /**
     * android1.x-6.x
     */
    private void startInstall() {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.parse("file://" + mPath), "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mAct.startActivity(install);
    }

    /**
     * android7.x
     */
    private void startInstallN() {
        //参数1 上下文, 参数2 在AndroidManifest中的android:authorities值, 参数3  共享的文件
        Uri apkUri = FileProvider.getUriForFile(mAct, "com.dev.fileprovider", new File(mPath));
        Intent install = new Intent(Intent.ACTION_VIEW);
        //由于没有在Activity环境下启动Activity,设置下面的标签
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //添加这一句表示对目标应用临时授权该Uri所代表的文件
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        install.setDataAndType(apkUri, "application/vnd.android.package-archive");
        mAct.startActivity(install);
    }

    /**
     * android8.x
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallO() {
        boolean isGranted = isSettingOpen(mAct);
        if (isGranted) startInstallN();//安装应用的逻辑(写自己的就可以)
        else new AlertDialog.Builder(mAct)
                .setCancelable(false)
                .setTitle("安装应用需要打开未知来源权限，请去设置中开启权限")
                .setPositiveButton("确定", (d, w) -> {
                    jumpToInstallSetting(mAct);
                })
                .show();
    }
    /**
     * 检查系统设置：是否允许安装来自未知来源的应用
     */
    private static boolean isSettingOpen(Context cxt) {
        boolean canInstall;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) // Android 8.0
            canInstall = cxt.getPackageManager().canRequestPackageInstalls();
        else
            canInstall = Settings.Secure.getInt(cxt.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS, 0) == 1;
        return canInstall;
    }
    /**
     * 跳转到系统设置：允许安装来自未知来源的应用
     */
    private static void jumpToInstallSetting(Context cxt) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) // Android 8.0
            cxt.startActivity(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + cxt.getPackageName())));
        else
            cxt.startActivity(new Intent(Settings.ACTION_SECURITY_SETTINGS));
    }
}
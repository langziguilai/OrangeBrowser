package com.dev.browser.utils;

import android.content.Context;
import android.os.Build;
import android.util.Base64;
import android.webkit.WebView;

import java.io.IOException;
import java.io.InputStream;

public class WebviewUtils {
    public static void injectScriptFile(WebView view, String scriptFile) {
        InputStream input;
        try {
            input = view.getContext().getAssets().open(scriptFile);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            String content="javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()";

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                view.evaluateJavascript(content,null);
            }else{
                view.loadUrl(content);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getInjectFileContent(Context context, String fileName) {
        InputStream input;
        try {
            input = context.getAssets().open(fileName);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

            // String-ify the script byte-array using BASE64 encoding !!!
           return new String(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static void injectCssFile(WebView view, String cssFile) {
        InputStream input;
        try {
            input = view.getContext().getAssets().open(cssFile);
            byte[] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            String content="javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()";
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                view.evaluateJavascript(content,null);
            }else{
                view.loadUrl(content);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

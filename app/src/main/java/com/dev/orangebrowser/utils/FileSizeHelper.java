package com.dev.orangebrowser.utils;

import java.text.DecimalFormat;

public class FileSizeHelper {
    public static String ShowLongFileSize(Long length) {
        DecimalFormat df = new DecimalFormat("0.00");//格式化小数
        if (length >= 1048576) {
            return df.format((float)length / 1048576) + "MB";
        } else if (length >= 1024) {
            return df.format((float)length / 1024) + "KB";
        } else if (length < 1024) {
            return length + "B";
        } else {
            return "0KB";
        }
    }
}

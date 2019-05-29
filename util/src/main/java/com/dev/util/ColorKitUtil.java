package com.dev.util;

public class ColorKitUtil{
    public static boolean isBackGroundLightMode(int color) {
        int red = (color & 0xff0000) >> 16;
        int green = (color & 0xff00) >> 8;
        int blue = color & 0xff;
        if (red > 230 && green > 230 && blue > 230) {
            return true;
        }
        return false;
    }
}


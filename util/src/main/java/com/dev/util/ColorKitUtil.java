package com.dev.util;

import androidx.core.graphics.ColorUtils;

public class ColorKitUtil{
    public static boolean isBackGroundLightMode(int color){
        float[] hsl=new float[3];
        ColorUtils.colorToHSL(color,hsl);
        float saturation=hsl[1];  //饱和度：范围 [0...1]
        float lightness=hsl[2];   //明度:范围  [0...1]
        //当饱和度小于0.1并且明度大于0.9时，判断为亮色背景
        return saturation < 0.1f && lightness > 0.9;
    }
}
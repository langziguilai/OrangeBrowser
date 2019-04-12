package com.dev.orangebrowser.databinding;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import androidx.databinding.BindingAdapter;
import com.dev.util.DensityUtil;

public class Util {
    @BindingAdapter("bgColor")
    public static void setViewBackgroundColor(View view, int color) {
        view.setBackgroundColor(color);
    }
    @BindingAdapter(value={"borderColor", "borderWidth"}, requireAll=false)
    public static void setViewBorderColor(View view, int color,float width) {
        GradientDrawable border = new GradientDrawable();
        border.setColor(Color.TRANSPARENT); //white background
        border.setStroke(DensityUtil.dip2px(view.getContext(),width), color); //black border with full opacity
        view.setBackground(border);
    }
}

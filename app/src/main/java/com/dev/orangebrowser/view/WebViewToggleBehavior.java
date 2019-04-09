package com.dev.orangebrowser.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.dev.orangebrowser.R;
import com.dev.util.DensityUtil;

import java.lang.ref.WeakReference;

public class WebViewToggleBehavior extends CoordinatorLayout.Behavior<View> {
    public WebViewToggleBehavior() {
    }

    public WebViewToggleBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static int NORMAL_SCREEN_MODE=1;  //正常形态
    public static int FULL_SCREEN_MODE=2;    //全局形态
    private int screenMode=FULL_SCREEN_MODE;

    private WeakReference<View> dependencyRef= new WeakReference<>(null);

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {

        boolean result= dependency.getId()== R.id.top_bar;
        if (result){
            dependencyRef=new WeakReference<View>(dependency);
        }
        return result;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
       // child.offsetTopAndBottom((int)dependency.getTranslationY()-(int)child.getTranslationY());
        child.setTranslationY((int)dependency.getTranslationY());
        //child.layout(0,dependency.getBottom(),child.getMeasuredWidth(),dependency.getBottom()+child.getMeasuredHeight());
        //child.setTop(dependency.getBottom());
        return true;
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        if (screenMode==FULL_SCREEN_MODE){
            if (dependencyRef.get()!=null){
                View dependency=dependencyRef.get();
                child.layout(0,dependency.getBottom(),child.getMeasuredWidth(),dependency.getBottom()+child.getMeasuredHeight());
            }
        }else{
            if (dependencyRef.get()!=null){
                View dependency=dependencyRef.get();
                child.layout(0,dependency.getBottom(),child.getMeasuredWidth(),dependency.getBottom()+child.getMeasuredHeight()-dependency.getMeasuredHeight()- DensityUtil.dip2px(parent.getContext(),42));
            }
        }
        return true;
    }

    public int getScreenMode() {
        return screenMode;
    }

    public void setScreenMode(int screenMode) {
        this.screenMode = screenMode;
    }
}

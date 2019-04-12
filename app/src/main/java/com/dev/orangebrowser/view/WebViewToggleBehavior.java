package com.dev.orangebrowser.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.dev.browser.session.Session;
import com.dev.orangebrowser.R;
import com.dev.util.DensityUtil;

import java.lang.ref.WeakReference;

public class WebViewToggleBehavior extends CoordinatorLayout.Behavior<View> {
    public WebViewToggleBehavior() {
    }

    public WebViewToggleBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private int screenMode= Session.NORMAL_SCREEN_MODE;//默认正常视野

    private WeakReference<View> topBarRef = new WeakReference<>(null);
    private WeakReference<View> bottomBarRef = new WeakReference<>(null);

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {

        boolean result= dependency.getId()== R.id.top_bar;
        if (result){
            topBarRef =new WeakReference<>(dependency);
        }
        if (dependency.getId()==R.id.bottom_bar){
            bottomBarRef=new WeakReference<>(dependency);
        }
        return result;
    }

    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        if (screenMode!=Session.NORMAL_SCREEN_MODE){
            child.setTop(dependency.getBottom());
        }
        return true;
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        if (screenMode==Session.NORMAL_SCREEN_MODE){
            if (topBarRef.get()!=null){
                View dependency= topBarRef.get();
                if (bottomBarRef.get()!=null){
                    child.layout(0,dependency.getBottom(),child.getMeasuredWidth(),parent.getBottom()+bottomBarRef.get().getMeasuredHeight());
                }else{
                    child.layout(0,dependency.getBottom(),child.getMeasuredWidth(),parent.getBottom());
                }

            }
        }else if (screenMode==Session.SCROLL_FULL_SCREEN_MODE){
            if (topBarRef.get()!=null){
                View dependency= topBarRef.get();
                child.layout(0,dependency.getBottom(),child.getMeasuredWidth(),dependency.getBottom()+child.getMeasuredHeight());
            }
        }else{
            if (topBarRef.get()!=null){
                View dependency= topBarRef.get();
                child.layout(0,dependency.getBottom(),child.getMeasuredWidth(),dependency.getBottom()+child.getMeasuredHeight());
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

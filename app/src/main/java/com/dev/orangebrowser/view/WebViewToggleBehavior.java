package com.dev.orangebrowser.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.dev.browser.session.Session;
import com.dev.orangebrowser.R;
import com.dev.orangebrowser.bloc.browser.integration.helper.WebViewVisionHelper;
import com.dev.util.DensityUtil;

import java.lang.ref.WeakReference;

public class WebViewToggleBehavior extends CoordinatorLayout.Behavior<View> {
    public WebViewToggleBehavior() {
    }

    public WebViewToggleBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Session session;
    private WebViewVisionHelper helper;
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
        if (session.getVisionMode()!=Session.NORMAL_SCREEN_MODE && session.getVisionMode()!=Session.STATIC_FULL_SCREEN_MODE){
            child.setTop(dependency.getBottom());
        }
        return true;
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        if (session.getVisionMode()==Session.NORMAL_SCREEN_MODE){
            if (topBarRef.get()!=null){
                View dependency= topBarRef.get();
                if (bottomBarRef.get()!=null){
                    child.layout(0,dependency.getBottom(),child.getMeasuredWidth(),parent.getBottom()+bottomBarRef.get().getMeasuredHeight());
                }else{
                    child.layout(0,dependency.getBottom(),child.getMeasuredWidth(),parent.getBottom());
                }

            }
        }else if (session.getVisionMode()==Session.SCROLL_FULL_SCREEN_MODE){
            if (topBarRef.get()!=null){
                View dependency= topBarRef.get();
                if (session.getEnterFullScreenMode()){
                    //为何要这样做：因为webview在coordinatorlayout中有时候跳转页面的时候，会触发onLayout，
                    // 并且dependency的位置会重置，恢复到默认状态，所以，在这里修复一下，如果是全屏模式：那么将dependency的位置调整到隐藏的位置，并将webview的容器大小设置为全屏
//                    int offset=dependency.getBottom();
//                    dependency.offsetTopAndBottom(-offset);
                    helper.restoreHideTopBar();
                    child.layout(0,0,child.getMeasuredWidth(),child.getMeasuredHeight());
                }else{
                    child.layout(0,dependency.getBottom(),child.getMeasuredWidth(),dependency.getBottom()+child.getMeasuredHeight());
                }
            }
        }else if(session.getVisionMode()==Session.MAX_SCREEN_MODE){
            if (topBarRef.get()!=null){
                View dependency= topBarRef.get();
                if (session.getEnterFullScreenMode()){
                    //为何要这样做：因为webview在coordinatorlayout中有时候跳转页面的时候，会触发onLayout，
                    // 并且dependency的位置会重置，恢复到默认状态，所以，在这里修复一下，如果是全屏模式：那么将dependency的位置调整到隐藏的位置，并将webview的容器大小设置为全屏
//                    int offset=dependency.getBottom();
//                    dependency.offsetTopAndBottom(-offset);
                    helper.restoreHideTopBar();
                    child.layout(0,0,child.getMeasuredWidth(),child.getMeasuredHeight());
                }else{
                    child.layout(0,dependency.getBottom(),child.getMeasuredWidth(),dependency.getBottom()+child.getMeasuredHeight());
                }
            }
        }else if(session.getVisionMode()==Session.STATIC_FULL_SCREEN_MODE){
            //全屏
                child.layout(0,0,parent.getMeasuredWidth(),parent.getMeasuredHeight());
        }
        return true;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public void setHelper(WebViewVisionHelper helper) {
        this.helper = helper;
    }
}

package com.dev.view.recyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import com.dev.util.Keep;

/*
* 解决RecyclerView嵌入在ViewPager中时，RecyclerView上下滑动时，若此时不取消滑动（抬起手ACTION_CANCEL，ACTION_UP），再左右滑动会导致Viewpager左右滑动
* 希望的效果时，当我在已经触发RecyclerView上下滑动时，ViewPager不应该再左右滑动
* */
@Keep
public class VerticalScrollRecyclerView extends androidx.recyclerview.widget.RecyclerView {
    private boolean interceptTouch=false;
    public VerticalScrollRecyclerView(@NonNull Context context) {
        super(context);
    }

    public VerticalScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalScrollRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // interceptTouch是自定义属性控制是否拦截事件
        if (interceptTouch){
            ViewParent parent =this;
            // 循环查找ViewPager, 请求ViewPager不拦截触摸事件
            while(!((parent = parent.getParent()) instanceof ViewPager)){
                // nop
            }
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void setInterceptTouch(boolean interceptTouch) {
        this.interceptTouch = interceptTouch;
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        detectCanViewPagerScroll(e);
        return super.onTouchEvent(e);
    }

    private float mLastTouchX =0f;
    private float mLastTouchY =0f;
    private boolean isIntercepted=false;
    private void detectCanViewPagerScroll(MotionEvent event){
        switch (event.getActionMasked()){
            case MotionEvent.ACTION_CANCEL:
                setInterceptTouch(false);
                break;
            case MotionEvent.ACTION_UP:
                setInterceptTouch(false);
                break;
            case MotionEvent.ACTION_DOWN:
                isIntercepted=false;
                mLastTouchX =event.getX();
                mLastTouchY =event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (!isIntercepted){
                    float x=Math.abs(event.getX()- mLastTouchX);
                    float y=Math.abs(event.getY()- mLastTouchY);
                    if(4*y>x && Math.max(4*y,x)> ViewConfiguration.getTouchSlop()){
                        setInterceptTouch(true);
                        isIntercepted=true;
                    }
                }
                break;
        }
    }

}

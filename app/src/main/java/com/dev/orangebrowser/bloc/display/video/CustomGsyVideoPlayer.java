package com.dev.orangebrowser.bloc.display.video;

import android.content.Context;
import android.util.AttributeSet;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

public class CustomGsyVideoPlayer extends StandardGSYVideoPlayer {
    public CustomGsyVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public CustomGsyVideoPlayer(Context context) {
        super(context);
    }

    public CustomGsyVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void hideUi(){
        hideAllWidget();
    }
}

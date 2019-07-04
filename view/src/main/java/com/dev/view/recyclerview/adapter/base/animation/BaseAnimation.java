package com.dev.view.recyclerview.adapter.base.animation;

import android.animation.Animator;
import android.view.View;
import com.dev.util.Keep;

/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
@Keep
public interface BaseAnimation {
    Animator[] getAnimators(View view);
}

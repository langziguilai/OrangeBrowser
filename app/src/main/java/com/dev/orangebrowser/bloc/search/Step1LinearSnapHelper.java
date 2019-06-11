package com.dev.orangebrowser.bloc.search;

import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class Step1LinearSnapHelper extends LinearSnapHelper {
    @Override
    public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
        int x= super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
        final int currentPosition = layoutManager.getPosition(findSnapView(layoutManager));
        return x>currentPosition ? currentPosition+1 : currentPosition-1;
    }
}

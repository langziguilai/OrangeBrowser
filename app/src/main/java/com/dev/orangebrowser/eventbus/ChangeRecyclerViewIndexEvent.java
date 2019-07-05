package com.dev.orangebrowser.eventbus;

import com.dev.util.Keep;

@Keep
public class ChangeRecyclerViewIndexEvent {
    public final int message;
    public ChangeRecyclerViewIndexEvent(int message) {
        this.message = message;
    }
}

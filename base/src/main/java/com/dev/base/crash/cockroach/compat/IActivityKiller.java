package com.dev.base.crash.cockroach.compat;

import android.os.Message;
import com.dev.util.Keep;

/**
 * Created by wanjian on 2018/5/24.
 */
@Keep
public interface IActivityKiller {

    void finishLaunchActivity(Message message);

    void finishResumeActivity(Message message);

    void finishPauseActivity(Message message);

    void finishStopActivity(Message message);


}

// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.popupwindow;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.widget.check_box.SmoothCheckBox;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ReadAdjustPop_ViewBinding implements Unbinder {
  private ReadAdjustPop target;

  @UiThread
  public ReadAdjustPop_ViewBinding(ReadAdjustPop target) {
    this(target, target);
  }

  @UiThread
  public ReadAdjustPop_ViewBinding(ReadAdjustPop target, View source) {
    this.target = target;

    target.vwBg = Utils.findRequiredView(source, R.id.vw_bg, "field 'vwBg'");
    target.hpbLight = Utils.findRequiredViewAsType(source, R.id.hpb_light, "field 'hpbLight'", SeekBar.class);
    target.scbFollowSys = Utils.findRequiredViewAsType(source, R.id.scb_follow_sys, "field 'scbFollowSys'", SmoothCheckBox.class);
    target.llFollowSys = Utils.findRequiredViewAsType(source, R.id.ll_follow_sys, "field 'llFollowSys'", LinearLayout.class);
    target.llClick = Utils.findRequiredViewAsType(source, R.id.ll_click, "field 'llClick'", LinearLayout.class);
    target.hpbClick = Utils.findRequiredViewAsType(source, R.id.hpb_click, "field 'hpbClick'", SeekBar.class);
    target.llTtsSpeechRate = Utils.findRequiredViewAsType(source, R.id.ll_tts_SpeechRate, "field 'llTtsSpeechRate'", LinearLayout.class);
    target.hpbTtsSpeechRate = Utils.findRequiredViewAsType(source, R.id.hpb_tts_SpeechRate, "field 'hpbTtsSpeechRate'", SeekBar.class);
    target.scbTtsFollowSys = Utils.findRequiredViewAsType(source, R.id.scb_tts_follow_sys, "field 'scbTtsFollowSys'", SmoothCheckBox.class);
    target.tvAutoPage = Utils.findRequiredViewAsType(source, R.id.tv_auto_page, "field 'tvAutoPage'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ReadAdjustPop target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.vwBg = null;
    target.hpbLight = null;
    target.scbFollowSys = null;
    target.llFollowSys = null;
    target.llClick = null;
    target.hpbClick = null;
    target.llTtsSpeechRate = null;
    target.hpbTtsSpeechRate = null;
    target.scbTtsFollowSys = null;
    target.tvAutoPage = null;
  }
}

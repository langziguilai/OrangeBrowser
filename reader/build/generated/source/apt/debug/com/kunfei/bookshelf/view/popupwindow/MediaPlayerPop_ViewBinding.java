// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.popupwindow;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.widget.views.ATESeekBar;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MediaPlayerPop_ViewBinding implements Unbinder {
  private MediaPlayerPop target;

  @UiThread
  public MediaPlayerPop_ViewBinding(MediaPlayerPop target) {
    this(target, target);
  }

  @UiThread
  public MediaPlayerPop_ViewBinding(MediaPlayerPop target, View source) {
    this.target = target;

    target.vwBg = Utils.findRequiredView(source, R.id.vw_bg, "field 'vwBg'");
    target.ivCover = Utils.findRequiredViewAsType(source, R.id.iv_cover, "field 'ivCover'", ImageView.class);
    target.tvDurTime = Utils.findRequiredViewAsType(source, R.id.tv_dur_time, "field 'tvDurTime'", TextView.class);
    target.seekBar = Utils.findRequiredViewAsType(source, R.id.player_progress, "field 'seekBar'", ATESeekBar.class);
    target.tvAllTime = Utils.findRequiredViewAsType(source, R.id.tv_all_time, "field 'tvAllTime'", TextView.class);
    target.ivSkipPrevious = Utils.findRequiredViewAsType(source, R.id.iv_skip_previous, "field 'ivSkipPrevious'", ImageView.class);
    target.fabPlayStop = Utils.findRequiredViewAsType(source, R.id.fab_play_stop, "field 'fabPlayStop'", FloatingActionButton.class);
    target.ivSkipNext = Utils.findRequiredViewAsType(source, R.id.iv_skip_next, "field 'ivSkipNext'", ImageView.class);
    target.ivTimer = Utils.findRequiredViewAsType(source, R.id.iv_timer, "field 'ivTimer'", ImageView.class);
    target.ivChapter = Utils.findRequiredViewAsType(source, R.id.iv_chapter, "field 'ivChapter'", ImageView.class);
    target.ivCoverBg = Utils.findRequiredViewAsType(source, R.id.iv_cover_bg, "field 'ivCoverBg'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MediaPlayerPop target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.vwBg = null;
    target.ivCover = null;
    target.tvDurTime = null;
    target.seekBar = null;
    target.tvAllTime = null;
    target.ivSkipPrevious = null;
    target.fabPlayStop = null;
    target.ivSkipNext = null;
    target.ivTimer = null;
    target.ivChapter = null;
    target.ivCoverBg = null;
  }
}

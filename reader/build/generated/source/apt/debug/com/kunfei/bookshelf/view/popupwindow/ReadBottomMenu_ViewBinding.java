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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kunfei.bookshelf.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ReadBottomMenu_ViewBinding implements Unbinder {
  private ReadBottomMenu target;

  @UiThread
  public ReadBottomMenu_ViewBinding(ReadBottomMenu target) {
    this(target, target);
  }

  @UiThread
  public ReadBottomMenu_ViewBinding(ReadBottomMenu target, View source) {
    this.target = target;

    target.vwBg = Utils.findRequiredView(source, R.id.vw_bg, "field 'vwBg'");
    target.fabReadAloudTimer = Utils.findRequiredViewAsType(source, R.id.fab_read_aloud_timer, "field 'fabReadAloudTimer'", FloatingActionButton.class);
    target.tvReadAloudTimer = Utils.findRequiredViewAsType(source, R.id.tv_read_aloud_timer, "field 'tvReadAloudTimer'", TextView.class);
    target.llReadAloudTimer = Utils.findRequiredViewAsType(source, R.id.ll_read_aloud_timer, "field 'llReadAloudTimer'", LinearLayout.class);
    target.fabReadAloud = Utils.findRequiredViewAsType(source, R.id.fabReadAloud, "field 'fabReadAloud'", FloatingActionButton.class);
    target.fabAutoPage = Utils.findRequiredViewAsType(source, R.id.fabAutoPage, "field 'fabAutoPage'", FloatingActionButton.class);
    target.fabReplaceRule = Utils.findRequiredViewAsType(source, R.id.fabReplaceRule, "field 'fabReplaceRule'", FloatingActionButton.class);
    target.fabNightTheme = Utils.findRequiredViewAsType(source, R.id.fabNightTheme, "field 'fabNightTheme'", FloatingActionButton.class);
    target.tvPre = Utils.findRequiredViewAsType(source, R.id.tv_pre, "field 'tvPre'", TextView.class);
    target.hpbReadProgress = Utils.findRequiredViewAsType(source, R.id.hpb_read_progress, "field 'hpbReadProgress'", SeekBar.class);
    target.tvNext = Utils.findRequiredViewAsType(source, R.id.tv_next, "field 'tvNext'", TextView.class);
    target.llCatalog = Utils.findRequiredViewAsType(source, R.id.ll_catalog, "field 'llCatalog'", LinearLayout.class);
    target.llAdjust = Utils.findRequiredViewAsType(source, R.id.ll_adjust, "field 'llAdjust'", LinearLayout.class);
    target.llFont = Utils.findRequiredViewAsType(source, R.id.ll_font, "field 'llFont'", LinearLayout.class);
    target.llSetting = Utils.findRequiredViewAsType(source, R.id.ll_setting, "field 'llSetting'", LinearLayout.class);
    target.llNavigationBar = Utils.findRequiredViewAsType(source, R.id.llNavigationBar, "field 'llNavigationBar'", LinearLayout.class);
    target.llFloatingButton = Utils.findRequiredViewAsType(source, R.id.ll_floating_button, "field 'llFloatingButton'", LinearLayout.class);
    target.vwNavigationBar = Utils.findRequiredView(source, R.id.vwNavigationBar, "field 'vwNavigationBar'");
  }

  @Override
  @CallSuper
  public void unbind() {
    ReadBottomMenu target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.vwBg = null;
    target.fabReadAloudTimer = null;
    target.tvReadAloudTimer = null;
    target.llReadAloudTimer = null;
    target.fabReadAloud = null;
    target.fabAutoPage = null;
    target.fabReplaceRule = null;
    target.fabNightTheme = null;
    target.tvPre = null;
    target.hpbReadProgress = null;
    target.tvNext = null;
    target.llCatalog = null;
    target.llAdjust = null;
    target.llFont = null;
    target.llSetting = null;
    target.llNavigationBar = null;
    target.llFloatingButton = null;
    target.vwNavigationBar = null;
  }
}

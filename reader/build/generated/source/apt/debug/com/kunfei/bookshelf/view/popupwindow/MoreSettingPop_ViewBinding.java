// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.popupwindow;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.widget.views.ATESwitch;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MoreSettingPop_ViewBinding implements Unbinder {
  private MoreSettingPop target;

  @UiThread
  public MoreSettingPop_ViewBinding(MoreSettingPop target) {
    this(target, target);
  }

  @UiThread
  public MoreSettingPop_ViewBinding(MoreSettingPop target, View source) {
    this.target = target;

    target.vwBg = Utils.findRequiredView(source, R.id.vw_bg, "field 'vwBg'");
    target.sbClickAllNext = Utils.findRequiredViewAsType(source, R.id.sb_click_all_next, "field 'sbClickAllNext'", Switch.class);
    target.sbClick = Utils.findRequiredViewAsType(source, R.id.sb_click, "field 'sbClick'", Switch.class);
    target.sbShowTitle = Utils.findRequiredViewAsType(source, R.id.sb_show_title, "field 'sbShowTitle'", Switch.class);
    target.sbShowTimeBattery = Utils.findRequiredViewAsType(source, R.id.sb_showTimeBattery, "field 'sbShowTimeBattery'", Switch.class);
    target.sbHideStatusBar = Utils.findRequiredViewAsType(source, R.id.sb_hideStatusBar, "field 'sbHideStatusBar'", Switch.class);
    target.llHideStatusBar = Utils.findRequiredViewAsType(source, R.id.ll_hideStatusBar, "field 'llHideStatusBar'", LinearLayout.class);
    target.llShowTimeBattery = Utils.findRequiredViewAsType(source, R.id.ll_showTimeBattery, "field 'llShowTimeBattery'", LinearLayout.class);
    target.sbHideNavigationBar = Utils.findRequiredViewAsType(source, R.id.sb_hideNavigationBar, "field 'sbHideNavigationBar'", Switch.class);
    target.llHideNavigationBar = Utils.findRequiredViewAsType(source, R.id.ll_hideNavigationBar, "field 'llHideNavigationBar'", LinearLayout.class);
    target.sbShowLine = Utils.findRequiredViewAsType(source, R.id.sb_showLine, "field 'sbShowLine'", Switch.class);
    target.llScreenTimeOut = Utils.findRequiredViewAsType(source, R.id.llScreenTimeOut, "field 'llScreenTimeOut'", LinearLayout.class);
    target.tvScreenTimeOut = Utils.findRequiredViewAsType(source, R.id.tv_screen_time_out, "field 'tvScreenTimeOut'", TextView.class);
    target.tvJFConvert = Utils.findRequiredViewAsType(source, R.id.tvJFConvert, "field 'tvJFConvert'", TextView.class);
    target.llJFConvert = Utils.findRequiredViewAsType(source, R.id.llJFConvert, "field 'llJFConvert'", LinearLayout.class);
    target.tvScreenDirection = Utils.findRequiredViewAsType(source, R.id.tv_screen_direction, "field 'tvScreenDirection'", TextView.class);
    target.llScreenDirection = Utils.findRequiredViewAsType(source, R.id.ll_screen_direction, "field 'llScreenDirection'", LinearLayout.class);
    target.swVolumeNextPage = Utils.findRequiredViewAsType(source, R.id.sw_volume_next_page, "field 'swVolumeNextPage'", Switch.class);
    target.swReadAloudKey = Utils.findRequiredViewAsType(source, R.id.sw_read_aloud_key, "field 'swReadAloudKey'", Switch.class);
    target.llReadAloudKey = Utils.findRequiredViewAsType(source, R.id.ll_read_aloud_key, "field 'llReadAloudKey'", LinearLayout.class);
    target.llClickAllNext = Utils.findRequiredViewAsType(source, R.id.ll_click_all_next, "field 'llClickAllNext'", LinearLayout.class);
    target.reNavBarColor = Utils.findRequiredViewAsType(source, R.id.reNavBarColor, "field 'reNavBarColor'", TextView.class);
    target.reNavBarColorVal = Utils.findRequiredViewAsType(source, R.id.reNavBarColor_val, "field 'reNavBarColorVal'", TextView.class);
    target.llNavigationBarColor = Utils.findRequiredViewAsType(source, R.id.llNavigationBarColor, "field 'llNavigationBarColor'", LinearLayout.class);
    target.sbImmersionStatusBar = Utils.findRequiredViewAsType(source, R.id.sbImmersionStatusBar, "field 'sbImmersionStatusBar'", ATESwitch.class);
    target.llImmersionStatusBar = Utils.findRequiredViewAsType(source, R.id.llImmersionStatusBar, "field 'llImmersionStatusBar'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MoreSettingPop target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.vwBg = null;
    target.sbClickAllNext = null;
    target.sbClick = null;
    target.sbShowTitle = null;
    target.sbShowTimeBattery = null;
    target.sbHideStatusBar = null;
    target.llHideStatusBar = null;
    target.llShowTimeBattery = null;
    target.sbHideNavigationBar = null;
    target.llHideNavigationBar = null;
    target.sbShowLine = null;
    target.llScreenTimeOut = null;
    target.tvScreenTimeOut = null;
    target.tvJFConvert = null;
    target.llJFConvert = null;
    target.tvScreenDirection = null;
    target.llScreenDirection = null;
    target.swVolumeNextPage = null;
    target.swReadAloudKey = null;
    target.llReadAloudKey = null;
    target.llClickAllNext = null;
    target.reNavBarColor = null;
    target.reNavBarColorVal = null;
    target.llNavigationBarColor = null;
    target.sbImmersionStatusBar = null;
    target.llImmersionStatusBar = null;
  }
}

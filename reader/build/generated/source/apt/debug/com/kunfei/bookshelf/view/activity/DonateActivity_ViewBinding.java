// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class DonateActivity_ViewBinding implements Unbinder {
  private DonateActivity target;

  @UiThread
  public DonateActivity_ViewBinding(DonateActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public DonateActivity_ViewBinding(DonateActivity target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.cvWxGzh = Utils.findRequiredViewAsType(source, R.id.cv_wx_gzh, "field 'cvWxGzh'", CardView.class);
    target.vwZfbTz = Utils.findRequiredViewAsType(source, R.id.vw_zfb_tz, "field 'vwZfbTz'", CardView.class);
    target.vwZfbHb = Utils.findRequiredViewAsType(source, R.id.vw_zfb_hb, "field 'vwZfbHb'", CardView.class);
    target.vwZfbRwm = Utils.findRequiredViewAsType(source, R.id.vw_zfb_rwm, "field 'vwZfbRwm'", CardView.class);
    target.vwWxRwm = Utils.findRequiredViewAsType(source, R.id.vw_wx_rwm, "field 'vwWxRwm'", CardView.class);
    target.llContent = Utils.findRequiredViewAsType(source, R.id.ll_content, "field 'llContent'", LinearLayout.class);
    target.vwQqRwm = Utils.findRequiredViewAsType(source, R.id.vw_qq_rwm, "field 'vwQqRwm'", CardView.class);
    target.vwZfbHbSsm = Utils.findRequiredViewAsType(source, R.id.vw_zfb_hb_ssm, "field 'vwZfbHbSsm'", CardView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    DonateActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.cvWxGzh = null;
    target.vwZfbTz = null;
    target.vwZfbHb = null;
    target.vwZfbRwm = null;
    target.vwWxRwm = null;
    target.llContent = null;
    target.vwQqRwm = null;
    target.vwZfbHbSsm = null;
  }
}

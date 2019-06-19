// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ReadStyleActivity_ViewBinding implements Unbinder {
  private ReadStyleActivity target;

  @UiThread
  public ReadStyleActivity_ViewBinding(ReadStyleActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ReadStyleActivity_ViewBinding(ReadStyleActivity target, View source) {
    this.target = target;

    target.llContent = Utils.findRequiredViewAsType(source, R.id.ll_content, "field 'llContent'", LinearLayout.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.tvContent = Utils.findRequiredViewAsType(source, R.id.tv_content, "field 'tvContent'", TextView.class);
    target.tvSelectTextColor = Utils.findRequiredViewAsType(source, R.id.tvSelectTextColor, "field 'tvSelectTextColor'", TextView.class);
    target.tvSelectBgColor = Utils.findRequiredViewAsType(source, R.id.tvSelectBgColor, "field 'tvSelectBgColor'", TextView.class);
    target.tvSelectBgImage = Utils.findRequiredViewAsType(source, R.id.tvSelectBgImage, "field 'tvSelectBgImage'", TextView.class);
    target.tvDefault = Utils.findRequiredViewAsType(source, R.id.tvDefault, "field 'tvDefault'", TextView.class);
    target.swDarkStatusIcon = Utils.findRequiredViewAsType(source, R.id.sw_darkStatusIcon, "field 'swDarkStatusIcon'", Switch.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ReadStyleActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.llContent = null;
    target.toolbar = null;
    target.tvContent = null;
    target.tvSelectTextColor = null;
    target.tvSelectBgColor = null;
    target.tvSelectBgImage = null;
    target.tvDefault = null;
    target.swDarkStatusIcon = null;
  }
}

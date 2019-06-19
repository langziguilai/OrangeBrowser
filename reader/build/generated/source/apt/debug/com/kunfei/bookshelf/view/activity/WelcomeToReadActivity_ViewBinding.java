// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import android.widget.ImageView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class WelcomeToReadActivity_ViewBinding implements Unbinder {
  private WelcomeToReadActivity target;

  @UiThread
  public WelcomeToReadActivity_ViewBinding(WelcomeToReadActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public WelcomeToReadActivity_ViewBinding(WelcomeToReadActivity target, View source) {
    this.target = target;

    target.ivBg = Utils.findRequiredViewAsType(source, R.id.iv_bg, "field 'ivBg'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    WelcomeToReadActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.ivBg = null;
  }
}

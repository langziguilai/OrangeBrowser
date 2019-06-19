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

public class WelcomeBookActivity_ViewBinding implements Unbinder {
  private WelcomeBookActivity target;

  @UiThread
  public WelcomeBookActivity_ViewBinding(WelcomeBookActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public WelcomeBookActivity_ViewBinding(WelcomeBookActivity target, View source) {
    this.target = target;

    target.ivBg = Utils.findRequiredViewAsType(source, R.id.iv_bg, "field 'ivBg'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    WelcomeBookActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.ivBg = null;
  }
}

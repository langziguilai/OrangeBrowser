// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.base;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.viewpager.widget.ViewPager;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.google.android.material.tabs.TabLayout;
import com.kunfei.bookshelf.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BaseTabActivity_ViewBinding implements Unbinder {
  private BaseTabActivity target;

  @UiThread
  public BaseTabActivity_ViewBinding(BaseTabActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public BaseTabActivity_ViewBinding(BaseTabActivity target, View source) {
    this.target = target;

    target.mTlIndicator = Utils.findRequiredViewAsType(source, R.id.tab_tl_indicator, "field 'mTlIndicator'", TabLayout.class);
    target.mVp = Utils.findRequiredViewAsType(source, R.id.tab_vp, "field 'mVp'", ViewPager.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BaseTabActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTlIndicator = null;
    target.mVp = null;
  }
}

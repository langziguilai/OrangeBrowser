// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.kunfei.bookshelf.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SourceLoginActivity_ViewBinding implements Unbinder {
  private SourceLoginActivity target;

  @UiThread
  public SourceLoginActivity_ViewBinding(SourceLoginActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SourceLoginActivity_ViewBinding(SourceLoginActivity target, View source) {
    this.target = target;

    target.webView = Utils.findRequiredViewAsType(source, R.id.web_view, "field 'webView'", WebView.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.actionBar = Utils.findRequiredViewAsType(source, R.id.action_bar, "field 'actionBar'", AppBarLayout.class);
    target.llContent = Utils.findRequiredViewAsType(source, R.id.ll_content, "field 'llContent'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SourceLoginActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.webView = null;
    target.toolbar = null;
    target.actionBar = null;
    target.llContent = null;
  }
}

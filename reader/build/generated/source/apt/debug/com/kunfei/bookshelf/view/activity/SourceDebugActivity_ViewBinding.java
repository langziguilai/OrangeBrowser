// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.widget.RotateLoading;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SourceDebugActivity_ViewBinding implements Unbinder {
  private SourceDebugActivity target;

  @UiThread
  public SourceDebugActivity_ViewBinding(SourceDebugActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SourceDebugActivity_ViewBinding(SourceDebugActivity target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.searchView = Utils.findRequiredViewAsType(source, R.id.searchView, "field 'searchView'", SearchView.class);
    target.loading = Utils.findRequiredViewAsType(source, R.id.loading, "field 'loading'", RotateLoading.class);
    target.actionBar = Utils.findRequiredViewAsType(source, R.id.action_bar, "field 'actionBar'", AppBarLayout.class);
    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.lv_log, "field 'recyclerView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SourceDebugActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.searchView = null;
    target.loading = null;
    target.actionBar = null;
    target.recyclerView = null;
  }
}

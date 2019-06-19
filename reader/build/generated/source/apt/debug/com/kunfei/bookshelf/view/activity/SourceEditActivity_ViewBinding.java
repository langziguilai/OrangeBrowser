// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.widget.views.ATECheckBox;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SourceEditActivity_ViewBinding implements Unbinder {
  private SourceEditActivity target;

  @UiThread
  public SourceEditActivity_ViewBinding(SourceEditActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SourceEditActivity_ViewBinding(SourceEditActivity target, View source) {
    this.target = target;

    target.actionBar = Utils.findRequiredViewAsType(source, R.id.action_bar, "field 'actionBar'", AppBarLayout.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.llContent = Utils.findRequiredViewAsType(source, R.id.ll_content, "field 'llContent'", LinearLayout.class);
    target.cbIsAudio = Utils.findRequiredViewAsType(source, R.id.cb_is_audio, "field 'cbIsAudio'", ATECheckBox.class);
    target.cbIsEnable = Utils.findRequiredViewAsType(source, R.id.cb_is_enable, "field 'cbIsEnable'", ATECheckBox.class);
    target.tvEditFind = Utils.findRequiredViewAsType(source, R.id.tv_edit_find, "field 'tvEditFind'", TextView.class);
    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.recycler_view, "field 'recyclerView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SourceEditActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.actionBar = null;
    target.toolbar = null;
    target.llContent = null;
    target.cbIsAudio = null;
    target.cbIsEnable = null;
    target.tvEditFind = null;
    target.recyclerView = null;
  }
}

// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class TxtChapterRuleActivity_ViewBinding implements Unbinder {
  private TxtChapterRuleActivity target;

  @UiThread
  public TxtChapterRuleActivity_ViewBinding(TxtChapterRuleActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public TxtChapterRuleActivity_ViewBinding(TxtChapterRuleActivity target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.llContent = Utils.findRequiredViewAsType(source, R.id.ll_content, "field 'llContent'", LinearLayout.class);
    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.recycler_view, "field 'recyclerView'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    TxtChapterRuleActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.llContent = null;
    target.recyclerView = null;
  }
}

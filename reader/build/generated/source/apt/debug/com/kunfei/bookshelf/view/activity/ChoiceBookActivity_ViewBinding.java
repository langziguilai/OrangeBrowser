// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.widget.recycler.refresh.RefreshRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ChoiceBookActivity_ViewBinding implements Unbinder {
  private ChoiceBookActivity target;

  @UiThread
  public ChoiceBookActivity_ViewBinding(ChoiceBookActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ChoiceBookActivity_ViewBinding(ChoiceBookActivity target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.rfRvSearchBooks = Utils.findRequiredViewAsType(source, R.id.rfRv_search_books, "field 'rfRvSearchBooks'", RefreshRecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ChoiceBookActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.rfRvSearchBooks = null;
  }
}

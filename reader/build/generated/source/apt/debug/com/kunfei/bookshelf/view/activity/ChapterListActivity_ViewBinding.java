// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.base.BaseTabActivity_ViewBinding;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ChapterListActivity_ViewBinding extends BaseTabActivity_ViewBinding {
  private ChapterListActivity target;

  @UiThread
  public ChapterListActivity_ViewBinding(ChapterListActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ChapterListActivity_ViewBinding(ChapterListActivity target, View source) {
    super(target, source);

    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
  }

  @Override
  public void unbind() {
    ChapterListActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;

    super.unbind();
  }
}

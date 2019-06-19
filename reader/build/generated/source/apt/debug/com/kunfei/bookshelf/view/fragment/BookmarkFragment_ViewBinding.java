// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.fragment;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.widget.recycler.scroller.FastScrollRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BookmarkFragment_ViewBinding implements Unbinder {
  private BookmarkFragment target;

  @UiThread
  public BookmarkFragment_ViewBinding(BookmarkFragment target, View source) {
    this.target = target;

    target.rvList = Utils.findRequiredViewAsType(source, R.id.rv_list, "field 'rvList'", FastScrollRecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BookmarkFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.rvList = null;
  }
}

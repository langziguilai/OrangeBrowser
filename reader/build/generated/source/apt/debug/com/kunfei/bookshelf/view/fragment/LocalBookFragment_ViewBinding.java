// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.fragment;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.widget.recycler.refresh.RefreshLayout;
import java.lang.IllegalStateException;
import java.lang.Override;

public class LocalBookFragment_ViewBinding implements Unbinder {
  private LocalBookFragment target;

  @UiThread
  public LocalBookFragment_ViewBinding(LocalBookFragment target, View source) {
    this.target = target;

    target.mRlRefresh = Utils.findRequiredViewAsType(source, R.id.refresh_layout, "field 'mRlRefresh'", RefreshLayout.class);
    target.mRvContent = Utils.findRequiredViewAsType(source, R.id.local_book_rv_content, "field 'mRvContent'", RecyclerView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    LocalBookFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mRlRefresh = null;
    target.mRvContent = null;
  }
}

// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.widget.recycler.refresh;

import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class RefreshRecyclerView_ViewBinding implements Unbinder {
  private RefreshRecyclerView target;

  @UiThread
  public RefreshRecyclerView_ViewBinding(RefreshRecyclerView target) {
    this(target, target);
  }

  @UiThread
  public RefreshRecyclerView_ViewBinding(RefreshRecyclerView target, View source) {
    this.target = target;

    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.recycler_view, "field 'recyclerView'", RecyclerView.class);
    target.rpb = Utils.findRequiredViewAsType(source, R.id.rpb, "field 'rpb'", RefreshProgressBar.class);
    target.llContent = Utils.findRequiredViewAsType(source, R.id.ll_content, "field 'llContent'", LinearLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    RefreshRecyclerView target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recyclerView = null;
    target.rpb = null;
    target.llContent = null;
  }
}

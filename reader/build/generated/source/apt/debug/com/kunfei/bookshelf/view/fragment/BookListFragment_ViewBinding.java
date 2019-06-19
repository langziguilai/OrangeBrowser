// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BookListFragment_ViewBinding implements Unbinder {
  private BookListFragment target;

  @UiThread
  public BookListFragment_ViewBinding(BookListFragment target, View source) {
    this.target = target;

    target.refreshLayout = Utils.findRequiredViewAsType(source, R.id.refresh_layout, "field 'refreshLayout'", SwipeRefreshLayout.class);
    target.rvBookshelf = Utils.findRequiredViewAsType(source, R.id.local_book_rv_content, "field 'rvBookshelf'", RecyclerView.class);
    target.tvEmpty = Utils.findRequiredViewAsType(source, R.id.tv_empty, "field 'tvEmpty'", TextView.class);
    target.rlEmptyView = Utils.findRequiredViewAsType(source, R.id.rl_empty_view, "field 'rlEmptyView'", RelativeLayout.class);
    target.ivBack = Utils.findRequiredViewAsType(source, R.id.iv_back, "field 'ivBack'", ImageView.class);
    target.actionBar = Utils.findRequiredViewAsType(source, R.id.action_bar, "field 'actionBar'", LinearLayout.class);
    target.tvSelectCount = Utils.findRequiredViewAsType(source, R.id.tv_select_count, "field 'tvSelectCount'", TextView.class);
    target.ivDel = Utils.findRequiredViewAsType(source, R.id.iv_del, "field 'ivDel'", ImageView.class);
    target.ivSelectAll = Utils.findRequiredViewAsType(source, R.id.iv_select_all, "field 'ivSelectAll'", ImageView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BookListFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.refreshLayout = null;
    target.rvBookshelf = null;
    target.tvEmpty = null;
    target.rlEmptyView = null;
    target.ivBack = null;
    target.actionBar = null;
    target.tvSelectCount = null;
    target.ivDel = null;
    target.ivSelectAll = null;
  }
}

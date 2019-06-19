// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.fragment;

import android.view.View;
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

public class FindBookFragment_ViewBinding implements Unbinder {
  private FindBookFragment target;

  @UiThread
  public FindBookFragment_ViewBinding(FindBookFragment target, View source) {
    this.target = target;

    target.llContent = Utils.findRequiredViewAsType(source, R.id.ll_content, "field 'llContent'", LinearLayout.class);
    target.refreshLayout = Utils.findRequiredViewAsType(source, R.id.refresh_layout, "field 'refreshLayout'", SwipeRefreshLayout.class);
    target.tvEmpty = Utils.findRequiredViewAsType(source, R.id.tv_empty, "field 'tvEmpty'", TextView.class);
    target.rlEmptyView = Utils.findRequiredViewAsType(source, R.id.rl_empty_view, "field 'rlEmptyView'", RelativeLayout.class);
    target.rvFindLeft = Utils.findRequiredViewAsType(source, R.id.rv_find_left, "field 'rvFindLeft'", RecyclerView.class);
    target.rvFindRight = Utils.findRequiredViewAsType(source, R.id.rv_find_right, "field 'rvFindRight'", RecyclerView.class);
    target.vwDivider = Utils.findRequiredView(source, R.id.vw_divider, "field 'vwDivider'");
  }

  @Override
  @CallSuper
  public void unbind() {
    FindBookFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.llContent = null;
    target.refreshLayout = null;
    target.tvEmpty = null;
    target.rlEmptyView = null;
    target.rvFindLeft = null;
    target.rvFindRight = null;
    target.vwDivider = null;
  }
}

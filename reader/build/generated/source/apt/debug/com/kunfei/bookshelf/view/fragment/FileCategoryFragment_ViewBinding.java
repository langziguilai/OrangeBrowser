// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.fragment;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FileCategoryFragment_ViewBinding implements Unbinder {
  private FileCategoryFragment target;

  @UiThread
  public FileCategoryFragment_ViewBinding(FileCategoryFragment target, View source) {
    this.target = target;

    target.mTvPath = Utils.findRequiredViewAsType(source, R.id.file_category_tv_path, "field 'mTvPath'", TextView.class);
    target.mTvBackLast = Utils.findRequiredViewAsType(source, R.id.file_category_tv_back_last, "field 'mTvBackLast'", TextView.class);
    target.mRvContent = Utils.findRequiredViewAsType(source, R.id.file_category_rv_content, "field 'mRvContent'", RecyclerView.class);
    target.tvSd = Utils.findRequiredViewAsType(source, R.id.tv_sd, "field 'tvSd'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    FileCategoryFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTvPath = null;
    target.mTvBackLast = null;
    target.mRvContent = null;
    target.tvSd = null;
  }
}

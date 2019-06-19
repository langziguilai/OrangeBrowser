// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.widget.recycler.scroller.FastScrollRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ChapterListFragment_ViewBinding implements Unbinder {
  private ChapterListFragment target;

  @UiThread
  public ChapterListFragment_ViewBinding(ChapterListFragment target, View source) {
    this.target = target;

    target.rvList = Utils.findRequiredViewAsType(source, R.id.rv_list, "field 'rvList'", FastScrollRecyclerView.class);
    target.tvChapterInfo = Utils.findRequiredViewAsType(source, R.id.tv_current_chapter_info, "field 'tvChapterInfo'", TextView.class);
    target.ivChapterTop = Utils.findRequiredViewAsType(source, R.id.iv_chapter_top, "field 'ivChapterTop'", ImageView.class);
    target.ivChapterBottom = Utils.findRequiredViewAsType(source, R.id.iv_chapter_bottom, "field 'ivChapterBottom'", ImageView.class);
    target.llBaseInfo = Utils.findRequiredView(source, R.id.ll_chapter_base_info, "field 'llBaseInfo'");
    target.vShadow = Utils.findRequiredView(source, R.id.v_shadow, "field 'vShadow'");
  }

  @Override
  @CallSuper
  public void unbind() {
    ChapterListFragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.rvList = null;
    target.tvChapterInfo = null;
    target.ivChapterTop = null;
    target.ivChapterBottom = null;
    target.llBaseInfo = null;
    target.vShadow = null;
  }
}

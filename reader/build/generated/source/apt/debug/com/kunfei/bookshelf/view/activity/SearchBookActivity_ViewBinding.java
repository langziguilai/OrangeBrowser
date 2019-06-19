// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.widget.recycler.refresh.RefreshRecyclerView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class SearchBookActivity_ViewBinding implements Unbinder {
  private SearchBookActivity target;

  @UiThread
  public SearchBookActivity_ViewBinding(SearchBookActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public SearchBookActivity_ViewBinding(SearchBookActivity target, View source) {
    this.target = target;

    target.searchView = Utils.findRequiredViewAsType(source, R.id.searchView, "field 'searchView'", SearchView.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.cardSearch = Utils.findRequiredViewAsType(source, R.id.card_search, "field 'cardSearch'", CardView.class);
    target.llSearchHistory = Utils.findRequiredViewAsType(source, R.id.ll_search_history, "field 'llSearchHistory'", LinearLayout.class);
    target.tvSearchHistoryClean = Utils.findRequiredViewAsType(source, R.id.tv_search_history_clean, "field 'tvSearchHistoryClean'", TextView.class);
    target.flSearchHistory = Utils.findRequiredViewAsType(source, R.id.tfl_search_history, "field 'flSearchHistory'", FlexboxLayout.class);
    target.rfRvSearchBooks = Utils.findRequiredViewAsType(source, R.id.rfRv_search_books, "field 'rfRvSearchBooks'", RefreshRecyclerView.class);
    target.fabSearchStop = Utils.findRequiredViewAsType(source, R.id.fabSearchStop, "field 'fabSearchStop'", FloatingActionButton.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    SearchBookActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.searchView = null;
    target.toolbar = null;
    target.cardSearch = null;
    target.llSearchHistory = null;
    target.tvSearchHistoryClean = null;
    target.flSearchHistory = null;
    target.rfRvSearchBooks = null;
    target.fabSearchStop = null;
  }
}

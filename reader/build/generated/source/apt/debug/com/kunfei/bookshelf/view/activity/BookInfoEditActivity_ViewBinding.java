// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.google.android.material.textfield.TextInputLayout;
import com.kunfei.bookshelf.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BookInfoEditActivity_ViewBinding implements Unbinder {
  private BookInfoEditActivity target;

  @UiThread
  public BookInfoEditActivity_ViewBinding(BookInfoEditActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public BookInfoEditActivity_ViewBinding(BookInfoEditActivity target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.ivCover = Utils.findRequiredViewAsType(source, R.id.iv_cover, "field 'ivCover'", ImageView.class);
    target.tieBookName = Utils.findRequiredViewAsType(source, R.id.tie_book_name, "field 'tieBookName'", EditText.class);
    target.tilBookName = Utils.findRequiredViewAsType(source, R.id.til_book_name, "field 'tilBookName'", TextInputLayout.class);
    target.tieBookAuthor = Utils.findRequiredViewAsType(source, R.id.tie_book_author, "field 'tieBookAuthor'", EditText.class);
    target.tilBookAuthor = Utils.findRequiredViewAsType(source, R.id.til_book_author, "field 'tilBookAuthor'", TextInputLayout.class);
    target.tieCoverUrl = Utils.findRequiredViewAsType(source, R.id.tie_cover_url, "field 'tieCoverUrl'", EditText.class);
    target.tilCoverUrl = Utils.findRequiredViewAsType(source, R.id.til_cover_url, "field 'tilCoverUrl'", TextInputLayout.class);
    target.tvSelectCover = Utils.findRequiredViewAsType(source, R.id.tv_select_cover, "field 'tvSelectCover'", TextView.class);
    target.tvChangeCover = Utils.findRequiredViewAsType(source, R.id.tv_change_cover, "field 'tvChangeCover'", TextView.class);
    target.tvRefreshCover = Utils.findRequiredViewAsType(source, R.id.tv_refresh_cover, "field 'tvRefreshCover'", TextView.class);
    target.tieBookJj = Utils.findRequiredViewAsType(source, R.id.tie_book_jj, "field 'tieBookJj'", EditText.class);
    target.tilBookJj = Utils.findRequiredViewAsType(source, R.id.til_book_jj, "field 'tilBookJj'", TextInputLayout.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BookInfoEditActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.ivCover = null;
    target.tieBookName = null;
    target.tilBookName = null;
    target.tieBookAuthor = null;
    target.tilBookAuthor = null;
    target.tieCoverUrl = null;
    target.tilCoverUrl = null;
    target.tvSelectCover = null;
    target.tvChangeCover = null;
    target.tvRefreshCover = null;
    target.tieBookJj = null;
    target.tilBookJj = null;
  }
}

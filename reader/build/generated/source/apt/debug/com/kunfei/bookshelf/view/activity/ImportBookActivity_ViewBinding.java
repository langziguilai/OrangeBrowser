// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.base.BaseTabActivity_ViewBinding;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ImportBookActivity_ViewBinding extends BaseTabActivity_ViewBinding {
  private ImportBookActivity target;

  @UiThread
  public ImportBookActivity_ViewBinding(ImportBookActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ImportBookActivity_ViewBinding(ImportBookActivity target, View source) {
    super(target, source);

    this.target = target;

    target.mCbSelectAll = Utils.findRequiredViewAsType(source, R.id.file_system_cb_selected_all, "field 'mCbSelectAll'", CheckBox.class);
    target.mBtnDelete = Utils.findRequiredViewAsType(source, R.id.file_system_btn_delete, "field 'mBtnDelete'", TextView.class);
    target.mBtnAddBook = Utils.findRequiredViewAsType(source, R.id.file_system_btn_add_book, "field 'mBtnAddBook'", TextView.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
  }

  @Override
  public void unbind() {
    ImportBookActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mCbSelectAll = null;
    target.mBtnDelete = null;
    target.mBtnAddBook = null;
    target.toolbar = null;

    super.unbind();
  }
}

// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.AppCompatImageView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.widget.CoverImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BookDetailActivity_ViewBinding implements Unbinder {
  private BookDetailActivity target;

  @UiThread
  public BookDetailActivity_ViewBinding(BookDetailActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public BookDetailActivity_ViewBinding(BookDetailActivity target, View source) {
    this.target = target;

    target.vwContent = Utils.findRequiredView(source, R.id.ifl_content, "field 'vwContent'");
    target.ivMenu = Utils.findRequiredViewAsType(source, R.id.iv_menu, "field 'ivMenu'", ImageView.class);
    target.ivBlurCover = Utils.findRequiredViewAsType(source, R.id.iv_blur_cover, "field 'ivBlurCover'", AppCompatImageView.class);
    target.ivCover = Utils.findRequiredViewAsType(source, R.id.iv_cover, "field 'ivCover'", CoverImageView.class);
    target.tvName = Utils.findRequiredViewAsType(source, R.id.tv_name, "field 'tvName'", TextView.class);
    target.tvAuthor = Utils.findRequiredViewAsType(source, R.id.tv_author, "field 'tvAuthor'", TextView.class);
    target.tvOrigin = Utils.findRequiredViewAsType(source, R.id.tv_origin, "field 'tvOrigin'", TextView.class);
    target.ivWeb = Utils.findRequiredViewAsType(source, R.id.iv_web, "field 'ivWeb'", ImageView.class);
    target.tvChapter = Utils.findRequiredViewAsType(source, R.id.tv_chapter, "field 'tvChapter'", TextView.class);
    target.tvIntro = Utils.findRequiredViewAsType(source, R.id.tv_intro, "field 'tvIntro'", TextView.class);
    target.tvShelf = Utils.findRequiredViewAsType(source, R.id.tv_shelf, "field 'tvShelf'", TextView.class);
    target.tvRead = Utils.findRequiredViewAsType(source, R.id.tv_read, "field 'tvRead'", TextView.class);
    target.tvLoading = Utils.findRequiredViewAsType(source, R.id.tv_loading, "field 'tvLoading'", TextView.class);
    target.tvChangeOrigin = Utils.findRequiredViewAsType(source, R.id.tv_change_origin, "field 'tvChangeOrigin'", TextView.class);
    target.rgBookGroup = Utils.findRequiredViewAsType(source, R.id.rg_book_group, "field 'rgBookGroup'", RadioGroup.class);
    target.tvChapterSize = Utils.findRequiredViewAsType(source, R.id.tv_chapter_size, "field 'tvChapterSize'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BookDetailActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.vwContent = null;
    target.ivMenu = null;
    target.ivBlurCover = null;
    target.ivCover = null;
    target.tvName = null;
    target.tvAuthor = null;
    target.tvOrigin = null;
    target.ivWeb = null;
    target.tvChapter = null;
    target.tvIntro = null;
    target.tvShelf = null;
    target.tvRead = null;
    target.tvLoading = null;
    target.tvChangeOrigin = null;
    target.rgBookGroup = null;
    target.tvChapterSize = null;
  }
}

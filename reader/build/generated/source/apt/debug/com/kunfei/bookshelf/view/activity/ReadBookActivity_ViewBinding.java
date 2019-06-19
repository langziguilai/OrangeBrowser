// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.google.android.material.appbar.AppBarLayout;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.view.popupwindow.MediaPlayerPop;
import com.kunfei.bookshelf.view.popupwindow.MoreSettingPop;
import com.kunfei.bookshelf.view.popupwindow.ReadAdjustPop;
import com.kunfei.bookshelf.view.popupwindow.ReadBottomMenu;
import com.kunfei.bookshelf.view.popupwindow.ReadInterfacePop;
import com.kunfei.bookshelf.widget.page.PageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ReadBookActivity_ViewBinding implements Unbinder {
  private ReadBookActivity target;

  @UiThread
  public ReadBookActivity_ViewBinding(ReadBookActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ReadBookActivity_ViewBinding(ReadBookActivity target, View source) {
    this.target = target;

    target.flContent = Utils.findRequiredViewAsType(source, R.id.fl_content, "field 'flContent'", FrameLayout.class);
    target.flMenu = Utils.findRequiredViewAsType(source, R.id.fl_menu, "field 'flMenu'", FrameLayout.class);
    target.vMenuBg = Utils.findRequiredView(source, R.id.v_menu_bg, "field 'vMenuBg'");
    target.readBottomMenu = Utils.findRequiredViewAsType(source, R.id.read_menu_bottom, "field 'readBottomMenu'", ReadBottomMenu.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.tvChapterName = Utils.findRequiredViewAsType(source, R.id.tv_chapter_name, "field 'tvChapterName'", TextView.class);
    target.tvUrl = Utils.findRequiredViewAsType(source, R.id.tv_chapter_url, "field 'tvUrl'", TextView.class);
    target.atvLine = Utils.findRequiredView(source, R.id.atv_line, "field 'atvLine'");
    target.llMenuTop = Utils.findRequiredViewAsType(source, R.id.ll_menu_top, "field 'llMenuTop'", LinearLayout.class);
    target.appBar = Utils.findRequiredViewAsType(source, R.id.appBar, "field 'appBar'", AppBarLayout.class);
    target.llISB = Utils.findRequiredViewAsType(source, R.id.ll_ISB, "field 'llISB'", LinearLayout.class);
    target.pageView = Utils.findRequiredViewAsType(source, R.id.pageView, "field 'pageView'", PageView.class);
    target.readAdjustPop = Utils.findRequiredViewAsType(source, R.id.readAdjustPop, "field 'readAdjustPop'", ReadAdjustPop.class);
    target.readInterfacePop = Utils.findRequiredViewAsType(source, R.id.readInterfacePop, "field 'readInterfacePop'", ReadInterfacePop.class);
    target.moreSettingPop = Utils.findRequiredViewAsType(source, R.id.moreSettingPop, "field 'moreSettingPop'", MoreSettingPop.class);
    target.progressBarNextPage = Utils.findRequiredViewAsType(source, R.id.pb_nextPage, "field 'progressBarNextPage'", ProgressBar.class);
    target.mediaPlayerPop = Utils.findRequiredViewAsType(source, R.id.mediaPlayerPop, "field 'mediaPlayerPop'", MediaPlayerPop.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ReadBookActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.flContent = null;
    target.flMenu = null;
    target.vMenuBg = null;
    target.readBottomMenu = null;
    target.toolbar = null;
    target.tvChapterName = null;
    target.tvUrl = null;
    target.atvLine = null;
    target.llMenuTop = null;
    target.appBar = null;
    target.llISB = null;
    target.pageView = null;
    target.readAdjustPop = null;
    target.readInterfacePop = null;
    target.moreSettingPop = null;
    target.progressBarNextPage = null;
    target.mediaPlayerPop = null;
  }
}

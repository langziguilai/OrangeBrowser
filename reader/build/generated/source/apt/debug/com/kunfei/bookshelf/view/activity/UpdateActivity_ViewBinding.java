// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class UpdateActivity_ViewBinding implements Unbinder {
  private UpdateActivity target;

  @UiThread
  public UpdateActivity_ViewBinding(UpdateActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public UpdateActivity_ViewBinding(UpdateActivity target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.tvMarkdown = Utils.findRequiredViewAsType(source, R.id.tv_markdown, "field 'tvMarkdown'", TextView.class);
    target.llContent = Utils.findRequiredViewAsType(source, R.id.ll_content, "field 'llContent'", LinearLayout.class);
    target.tvDownloadProgress = Utils.findRequiredViewAsType(source, R.id.tv_download_progress, "field 'tvDownloadProgress'", TextView.class);
    target.llDownload = Utils.findRequiredViewAsType(source, R.id.ll_download, "field 'llDownload'", LinearLayout.class);
    target.progressBar = Utils.findRequiredViewAsType(source, R.id.pb_download, "field 'progressBar'", ProgressBar.class);
    target.tvInstallUpdate = Utils.findRequiredViewAsType(source, R.id.tv_install_update, "field 'tvInstallUpdate'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    UpdateActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.tvMarkdown = null;
    target.llContent = null;
    target.tvDownloadProgress = null;
    target.llDownload = null;
    target.progressBar = null;
    target.tvInstallUpdate = null;
  }
}

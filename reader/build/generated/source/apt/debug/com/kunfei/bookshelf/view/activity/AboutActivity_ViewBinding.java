// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AboutActivity_ViewBinding implements Unbinder {
  private AboutActivity target;

  @UiThread
  public AboutActivity_ViewBinding(AboutActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public AboutActivity_ViewBinding(AboutActivity target, View source) {
    this.target = target;

    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.tvVersion = Utils.findRequiredViewAsType(source, R.id.tv_version, "field 'tvVersion'", TextView.class);
    target.vwVersion = Utils.findRequiredViewAsType(source, R.id.vw_version, "field 'vwVersion'", CardView.class);
    target.tvDonate = Utils.findRequiredViewAsType(source, R.id.tv_donate, "field 'tvDonate'", TextView.class);
    target.vwDonate = Utils.findRequiredViewAsType(source, R.id.vw_donate, "field 'vwDonate'", CardView.class);
    target.tvScoring = Utils.findRequiredViewAsType(source, R.id.tv_scoring, "field 'tvScoring'", TextView.class);
    target.vwScoring = Utils.findRequiredViewAsType(source, R.id.vw_scoring, "field 'vwScoring'", CardView.class);
    target.tvGit = Utils.findRequiredViewAsType(source, R.id.tv_git, "field 'tvGit'", TextView.class);
    target.vwGit = Utils.findRequiredViewAsType(source, R.id.vw_git, "field 'vwGit'", CardView.class);
    target.tvDisclaimer = Utils.findRequiredViewAsType(source, R.id.tv_disclaimer, "field 'tvDisclaimer'", TextView.class);
    target.vwDisclaimer = Utils.findRequiredViewAsType(source, R.id.vw_disclaimer, "field 'vwDisclaimer'", CardView.class);
    target.llContent = Utils.findRequiredViewAsType(source, R.id.ll_content, "field 'llContent'", LinearLayout.class);
    target.tvMail = Utils.findRequiredViewAsType(source, R.id.tv_mail, "field 'tvMail'", TextView.class);
    target.vwMail = Utils.findRequiredViewAsType(source, R.id.vw_mail, "field 'vwMail'", CardView.class);
    target.tvUpdate = Utils.findRequiredViewAsType(source, R.id.tv_update, "field 'tvUpdate'", TextView.class);
    target.vwUpdate = Utils.findRequiredViewAsType(source, R.id.vw_update, "field 'vwUpdate'", CardView.class);
    target.tvQq = Utils.findRequiredViewAsType(source, R.id.tv_qq, "field 'tvQq'", TextView.class);
    target.vwQq = Utils.findRequiredViewAsType(source, R.id.vw_qq, "field 'vwQq'", CardView.class);
    target.tvAppSummary = Utils.findRequiredViewAsType(source, R.id.tv_app_summary, "field 'tvAppSummary'", TextView.class);
    target.tvUpdateLog = Utils.findRequiredViewAsType(source, R.id.tv_update_log, "field 'tvUpdateLog'", TextView.class);
    target.vwUpdateLog = Utils.findRequiredViewAsType(source, R.id.vw_update_log, "field 'vwUpdateLog'", CardView.class);
    target.tvHomePage = Utils.findRequiredViewAsType(source, R.id.tv_home_page, "field 'tvHomePage'", TextView.class);
    target.vwHomePage = Utils.findRequiredViewAsType(source, R.id.vw_home_page, "field 'vwHomePage'", CardView.class);
    target.tvFaq = Utils.findRequiredViewAsType(source, R.id.tv_faq, "field 'tvFaq'", TextView.class);
    target.vwFaq = Utils.findRequiredViewAsType(source, R.id.vw_faq, "field 'vwFaq'", CardView.class);
    target.tvShare = Utils.findRequiredViewAsType(source, R.id.tv_share, "field 'tvShare'", TextView.class);
    target.vwShare = Utils.findRequiredViewAsType(source, R.id.vw_share, "field 'vwShare'", CardView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AboutActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.toolbar = null;
    target.tvVersion = null;
    target.vwVersion = null;
    target.tvDonate = null;
    target.vwDonate = null;
    target.tvScoring = null;
    target.vwScoring = null;
    target.tvGit = null;
    target.vwGit = null;
    target.tvDisclaimer = null;
    target.vwDisclaimer = null;
    target.llContent = null;
    target.tvMail = null;
    target.vwMail = null;
    target.tvUpdate = null;
    target.vwUpdate = null;
    target.tvQq = null;
    target.vwQq = null;
    target.tvAppSummary = null;
    target.tvUpdateLog = null;
    target.vwUpdateLog = null;
    target.tvHomePage = null;
    target.vwHomePage = null;
    target.tvFaq = null;
    target.vwFaq = null;
    target.tvShare = null;
    target.vwShare = null;
  }
}

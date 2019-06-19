// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.popupwindow;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.widget.number.NumberButton;
import de.hdodenhof.circleimageview.CircleImageView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ReadInterfacePop_ViewBinding implements Unbinder {
  private ReadInterfacePop target;

  @UiThread
  public ReadInterfacePop_ViewBinding(ReadInterfacePop target) {
    this(target, target);
  }

  @UiThread
  public ReadInterfacePop_ViewBinding(ReadInterfacePop target, View source) {
    this.target = target;

    target.vwBg = Utils.findRequiredView(source, R.id.vw_bg, "field 'vwBg'");
    target.flTextBold = Utils.findRequiredViewAsType(source, R.id.fl_text_Bold, "field 'flTextBold'", TextView.class);
    target.fl_text_font = Utils.findRequiredViewAsType(source, R.id.fl_text_font, "field 'fl_text_font'", TextView.class);
    target.civBgWhite = Utils.findRequiredViewAsType(source, R.id.civ_bg_white, "field 'civBgWhite'", CircleImageView.class);
    target.civBgYellow = Utils.findRequiredViewAsType(source, R.id.civ_bg_yellow, "field 'civBgYellow'", CircleImageView.class);
    target.civBgGreen = Utils.findRequiredViewAsType(source, R.id.civ_bg_green, "field 'civBgGreen'", CircleImageView.class);
    target.civBgBlack = Utils.findRequiredViewAsType(source, R.id.civ_bg_black, "field 'civBgBlack'", CircleImageView.class);
    target.civBgBlue = Utils.findRequiredViewAsType(source, R.id.civ_bg_blue, "field 'civBgBlue'", CircleImageView.class);
    target.tv0 = Utils.findRequiredViewAsType(source, R.id.tv0, "field 'tv0'", TextView.class);
    target.tv1 = Utils.findRequiredViewAsType(source, R.id.tv1, "field 'tv1'", TextView.class);
    target.tv2 = Utils.findRequiredViewAsType(source, R.id.tv2, "field 'tv2'", TextView.class);
    target.tv3 = Utils.findRequiredViewAsType(source, R.id.tv3, "field 'tv3'", TextView.class);
    target.tv4 = Utils.findRequiredViewAsType(source, R.id.tv4, "field 'tv4'", TextView.class);
    target.nbPaddingTop = Utils.findRequiredViewAsType(source, R.id.nbPaddingTop, "field 'nbPaddingTop'", NumberButton.class);
    target.nbPaddingBottom = Utils.findRequiredViewAsType(source, R.id.nbPaddingBottom, "field 'nbPaddingBottom'", NumberButton.class);
    target.nbPaddingLeft = Utils.findRequiredViewAsType(source, R.id.nbPaddingLeft, "field 'nbPaddingLeft'", NumberButton.class);
    target.nbPaddingRight = Utils.findRequiredViewAsType(source, R.id.nbPaddingRight, "field 'nbPaddingRight'", NumberButton.class);
    target.tvPageMode = Utils.findRequiredViewAsType(source, R.id.tvPageMode, "field 'tvPageMode'", TextView.class);
    target.nbTextSize = Utils.findRequiredViewAsType(source, R.id.nbTextSize, "field 'nbTextSize'", NumberButton.class);
    target.nbLineSize = Utils.findRequiredViewAsType(source, R.id.nbLineSize, "field 'nbLineSize'", NumberButton.class);
    target.nbParagraphSize = Utils.findRequiredViewAsType(source, R.id.nbParagraphSize, "field 'nbParagraphSize'", NumberButton.class);
    target.tvIndent = Utils.findRequiredViewAsType(source, R.id.fl_indent, "field 'tvIndent'", TextView.class);
    target.nbTipPaddingTop = Utils.findRequiredViewAsType(source, R.id.nbTipPaddingTop, "field 'nbTipPaddingTop'", NumberButton.class);
    target.nbTipPaddingBottom = Utils.findRequiredViewAsType(source, R.id.nbTipPaddingBottom, "field 'nbTipPaddingBottom'", NumberButton.class);
    target.nbTipPaddingLeft = Utils.findRequiredViewAsType(source, R.id.nbTipPaddingLeft, "field 'nbTipPaddingLeft'", NumberButton.class);
    target.nbTipPaddingRight = Utils.findRequiredViewAsType(source, R.id.nbTipPaddingRight, "field 'nbTipPaddingRight'", NumberButton.class);
    target.nbLetterSpacing = Utils.findRequiredViewAsType(source, R.id.nbLetterSpacing, "field 'nbLetterSpacing'", NumberButton.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ReadInterfacePop target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.vwBg = null;
    target.flTextBold = null;
    target.fl_text_font = null;
    target.civBgWhite = null;
    target.civBgYellow = null;
    target.civBgGreen = null;
    target.civBgBlack = null;
    target.civBgBlue = null;
    target.tv0 = null;
    target.tv1 = null;
    target.tv2 = null;
    target.tv3 = null;
    target.tv4 = null;
    target.nbPaddingTop = null;
    target.nbPaddingBottom = null;
    target.nbPaddingLeft = null;
    target.nbPaddingRight = null;
    target.tvPageMode = null;
    target.nbTextSize = null;
    target.nbLineSize = null;
    target.nbParagraphSize = null;
    target.tvIndent = null;
    target.nbTipPaddingTop = null;
    target.nbTipPaddingBottom = null;
    target.nbTipPaddingLeft = null;
    target.nbTipPaddingRight = null;
    target.nbLetterSpacing = null;
  }
}

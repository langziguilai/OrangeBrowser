// Generated code from Butter Knife. Do not modify!
package com.kunfei.bookshelf.view.activity;

import android.view.View;
import androidx.annotation.UiThread;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import butterknife.internal.Utils;
import com.google.android.material.navigation.NavigationView;
import com.kunfei.bookshelf.R;
import com.kunfei.bookshelf.base.BaseTabActivity_ViewBinding;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding extends BaseTabActivity_ViewBinding {
  private MainActivity target;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(MainActivity target, View source) {
    super(target, source);

    this.target = target;

    target.drawer = Utils.findRequiredViewAsType(source, R.id.drawer, "field 'drawer'", DrawerLayout.class);
    target.navigationView = Utils.findRequiredViewAsType(source, R.id.navigation_view, "field 'navigationView'", NavigationView.class);
    target.toolbar = Utils.findRequiredViewAsType(source, R.id.toolbar, "field 'toolbar'", Toolbar.class);
    target.mainView = Utils.findRequiredViewAsType(source, R.id.main_view, "field 'mainView'", CoordinatorLayout.class);
    target.cardSearch = Utils.findRequiredViewAsType(source, R.id.card_search, "field 'cardSearch'", CardView.class);
  }

  @Override
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.drawer = null;
    target.navigationView = null;
    target.toolbar = null;
    target.mainView = null;
    target.cardSearch = null;

    super.unbind();
  }
}

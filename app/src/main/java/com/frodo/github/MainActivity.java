package com.frodo.github;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.frodo.app.android.core.toolbox.ScreenUtils;
import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.app.framework.broadcast.LocalBroadcastManager;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.business.account.AccountModel;
import com.frodo.github.business.account.LoginFragment;
import com.frodo.github.business.account.ProfileFragment;
import com.frodo.github.business.explore.ExploreFragment;
import com.frodo.github.common.IconApiFragment;
import com.frodo.github.common.JsoupApiFragment;

/**
 * Created by frodo on 2016/4/28. Main Page
 */
public class MainActivity extends FragmentContainerActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View navigationHeadView;

    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FloatingActionButton fab;

    private AccountModel accountModel;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.id_nv_menu);
        navigationHeadView = navigationView.getHeaderView(0);

        navigationView.setPadding(0, ScreenUtils.getStatusHeight(this), 0, 0);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.syncState();
        fab = (FloatingActionButton) findViewById(R.id.fab);
    }

    @Override
    public void registerListener() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    MainActivity.super.onBackPressed();
                }
            }
        });
        navigationHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
                if (accountModel.isSignIn()) {
                    User user = accountModel.getSignInUser();
                    Bundle arguments = new Bundle();
                    arguments.putString("username", user.login);
                    FragmentScheduler.nextFragment(MainActivity.this, ProfileFragment.class, arguments);
                } else {
                    FragmentScheduler.nextFragment(MainActivity.this, LoginFragment.class);
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    private MenuItem mPreMenuItem;

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (mPreMenuItem != null) {
                            mPreMenuItem.setChecked(false);
                        }

                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        mPreMenuItem = menuItem;

                        toolbar.setTitle(menuItem.getTitle());
                        switch (menuItem.getItemId()) {
                            case R.id.action_sign_in:
                                FragmentScheduler.nextFragment(MainActivity.this, LoginFragment.class);
                                return true;
                            case R.id.action_sign_out:
                                navigationView.getMenu().clear();
                                navigationView.inflateMenu(R.menu.menu_drawer_not_signed);
                                return true;
                            case R.id.action_explore:
                                FragmentScheduler.replaceFragment(MainActivity.this, ExploreFragment.class);
                                return true;
                            case R.id.action_icon_api:
                                FragmentScheduler.replaceFragment(MainActivity.this, IconApiFragment.class);
                                return true;
                            case R.id.action_jsoup_api:
                                FragmentScheduler.replaceFragment(MainActivity.this, JsoupApiFragment.class);
                            default:
                                return true;
                        }
                    }
                });

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.contribute_description, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getMainController().getLocalBroadcastManager().register("drawer", new LocalBroadcastManager.MessageInterceptor() {
            @Override
            public Boolean intercept(Object o) {
                if (o instanceof Boolean) {
                    boolean isEnableShowDrawer = (boolean) o;
                    if (isEnableShowDrawer) {
                        actionBarDrawerToggle.onDrawerSlide(null, 0);
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNDEFINED);
                    } else {
                        actionBarDrawerToggle.onDrawerSlide(null, 1);
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    }
                } else if (o instanceof User) {
                    MainActivity.super.onBackPressed();
                    updateUserView();
                }
                return true;
            }
        });
    }

    @Override
    public void initBusiness() {
        FragmentScheduler.replaceFragment(this, MainFragment.class);

        accountModel = getMainController().getModelFactory()
                .getOrCreateIfAbsent(AccountModel.TAG, AccountModel.class, getMainController());
        updateUserView();
    }

    private void updateUserView() {
        if (accountModel.isSignIn()) {
            User user = accountModel.getSignInUser();
            ((SimpleDraweeView) navigationHeadView.findViewById(R.id.head_sdv)).setImageURI(Uri.parse(user.avatar_url));
            ((TextView) navigationHeadView.findViewById(R.id.id_username)).setText(user.login);
            ((TextView) navigationHeadView.findViewById(R.id.id_repo)).setText(user.html_url);

            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_drawer_already_signed);
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.menu_drawer_not_signed);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        getMainController().getLocalBroadcastManager().unRegisterGroup("drawer");
        super.onDestroy();
    }
}

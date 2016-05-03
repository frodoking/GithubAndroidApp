package com.frodo.github;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.frodo.app.android.core.toolbox.FragmentScheduler;
import com.frodo.app.android.core.toolbox.ScreenUtils;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.app.framework.broadcast.LocalBroadcastManager;
import com.frodo.github.business.explore.ExploreFragment;
import com.frodo.github.common.IconAPiFragment;

/**
 * Created by frodo on 2016/4/28. Main Page
 */
public class MainActivity extends FragmentContainerActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Toolbar toolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FloatingActionButton fab;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        drawerLayout = (DrawerLayout) findViewById(R.id.id_drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.id_nv_menu);

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
                drawerLayout.openDrawer(GravityCompat.START);
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
                            case R.id.action_icon_api:
                                FragmentScheduler.nextFragment(MainActivity.this, IconAPiFragment.class, null, true);
                                return true;
                            case R.id.action_explore:
                                FragmentScheduler.nextFragment(MainActivity.this, ExploreFragment.class, null, true);
                                return true;
                            default:
                                return true;
                        }
                    }
                });

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "This App is show Android Github Client By Frodo", Snackbar.LENGTH_LONG)
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
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
                    } else {
                        actionBarDrawerToggle.onDrawerSlide(null, 1);
                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void initBusiness() {
        FragmentScheduler.nextFragment(this, MainFragment.class, null, true);
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
        getMainController().getLocalBroadcastManager().unRegisterGroup("drawer");
        return super.onKeyDown(keyCode, event);
    }

}

package com.frodo.github;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.frodo.app.android.core.toolbox.FragmentScheduler;
import com.frodo.app.android.core.toolbox.ScreenUtils;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
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
        return R.layout.fragment_main;
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
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    private MenuItem mPreMenuItem;

                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if (mPreMenuItem != null) {
                            mPreMenuItem.setChecked(false);
                            if (mPreMenuItem.equals(menuItem)) {
                                return true;
                            }
                        }

                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        mPreMenuItem = menuItem;

                        toolbar.setTitle(menuItem.getTitle());
                        switch (menuItem.getItemId()) {
                            case R.id.action_icon_api:
                                FragmentScheduler.replaceFragment(MainActivity.this, IconAPiFragment.class, null);
                                return true;
                            case R.id.action_explore:
                                FragmentScheduler.replaceFragment(MainActivity.this, ExploreFragment.class, null);
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
    }

    @Override
    public void initBusiness() {
        FragmentScheduler.nextFragment(this, ExploreFragment.class, null, true);
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

}

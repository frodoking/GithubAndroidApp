package com.frodo.github;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.frodo.app.android.core.toolbox.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.github.business.explore.ExploreFragment;
import com.frodo.github.icon.IconAPiActivity;

import java.lang.reflect.Method;

/**
 * Created by frodo on 2016/4/28. Main Page
 */
public class MainActivity extends FragmentContainerActivity {
    @Override
    public void initView() {
    }

    @Override
    public void registerListener() {
    }

    @Override
    public void initBusiness() {
        FragmentScheduler.nextFragment(this, MainFragment.class, null, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_setting, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.isChecked()) return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_icon_api:
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.setClass(this, IconAPiActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_explore:
                item.setVisible(false);
                FragmentScheduler.nextFragment(this, ExploreFragment.class, null, false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

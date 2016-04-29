package com.frodo.github;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.core.UIView;
import com.frodo.app.android.ui.fragment.AbstractBaseFragment;

/**
 * Created by frodo on 2016/4/28.
 */
public class MainFragment extends AbstractBaseFragment {

    @Override
    public UIView createUIView(Context context, LayoutInflater layoutInflater, ViewGroup viewGroup) {
        return new UIView(this, layoutInflater, viewGroup, R.layout.fragment_main) {
            @Override
            public void initView() {
            }

            @Override
            public void registerListener() {
            }
        };
    }
}

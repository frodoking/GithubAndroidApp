package com.frodo.github;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.core.UIView;
import com.frodo.app.android.ui.fragment.StatedFragment;

/**
 * Created by frodo on 2016/4/28.
 */
public class MainFragment extends StatedFragment {

    @Override
    public UIView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new UIView(this, inflater, container, R.layout.fragment_main) {
            @Override
            public void initView() {
            }

            @Override
            public void registerListener() {
            }
        };
    }
}

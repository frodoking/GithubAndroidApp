package com.frodo.github.common;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.core.UIView;
import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.icon.OcticonsFontAwesome;
import com.frodo.github.icon.StaticOcticonsFragment;

/**
 * Created by frodo on 2016/6/1.
 */
public class OcticonsFragment extends StatedFragment {

    private StaticOcticonsFragment staticOcticonsFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        staticOcticonsFragment = new StaticOcticonsFragment(getAndroidContext(), OcticonsFontAwesome.TAG);
    }

    @Override
    public UIView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new UIView(this, inflater, container, staticOcticonsFragment.getLayoutId()) {
            @Override
            public void initView() {
                staticOcticonsFragment.initView(getView());
            }

            @Override
            public void registerListener() {
            }
        };
    }
}
package com.frodo.github.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.core.UIView;
import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.icon.IconAPi;

/**
 * Created by frodo on 2016/5/1.
 */
public class IconAPiFragment extends StatedFragment {

    private IconAPi iconAPi = new IconAPi();

    @Override
    public UIView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new UIView(this, inflater, container, iconAPi.getLayoutId()) {
            @Override
            public void initView() {
                iconAPi.initView(getView());
            }

            @Override
            public void registerListener() {
            }
        };
    }
}

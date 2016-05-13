package com.frodo.github.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.core.UIView;
import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.datasource.JsoupApi;

/**
 * Created by frodo on 2016/5/13.
 */
public class JsoupApiFragment extends StatedFragment {

    private JsoupApi jsoupApi = new JsoupApi();

    @Override
    public UIView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new UIView(this, inflater, container, jsoupApi.getLayoutId()) {
            @Override
            public void initView() {
                jsoupApi.initView(getView());
            }

            @Override
            public void registerListener() {
            }
        };
    }
}

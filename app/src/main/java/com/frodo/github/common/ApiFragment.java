package com.frodo.github.common;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.core.UIView;
import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.StaticFragment;

/**
 * Created by frodo on 2016/5/13.
 */
public class ApiFragment extends StatedFragment {
    String tag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag = getArguments().getString("api", "StaticOcticons");
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tag());
    }

    @Override
    public String tag() {
        return tag;
    }

    @Override
    public UIView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        final StaticFragment staticFragment = StaticFragment.get(context, tag);
        return new UIView(this, inflater, container, staticFragment.getLayoutId()) {
            @Override
            public void initView() {
                staticFragment.initView(getView());
            }

            @Override
            public void registerListener() {
            }
        };
    }
}

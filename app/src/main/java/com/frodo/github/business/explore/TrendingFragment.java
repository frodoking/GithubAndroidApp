package com.frodo.github.business.explore;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;

/**
 * Created by frodo on 16/6/10.
 */
public class TrendingFragment extends StatedFragment<TrendingView, ExploreModel> {

    @Override
    public TrendingView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new TrendingView(this, inflater, container);
    }
}

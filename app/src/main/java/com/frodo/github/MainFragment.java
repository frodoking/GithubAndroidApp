package com.frodo.github;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.core.UIView;
import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.view.FrescoAndIconicsImageView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by frodo on 2016/4/28.
 */
public class MainFragment extends StatedFragment {

    @Override
    public UIView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new UIView(this, inflater, container, R.layout.fragment_main) {

            @Override
            public void initView() {
                FrescoAndIconicsImageView sdv = (FrescoAndIconicsImageView) getRootView().findViewById(R.id.logo_fiiv);
                sdv.setImageURI(Uri.parse("https://developer.github.com/assets/images/electrocat.png"));

                AdView adView = (AdView) getRootView().findViewById(R.id.ad_view);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            }

            @Override
            public void registerListener() {
            }
        };
    }
}

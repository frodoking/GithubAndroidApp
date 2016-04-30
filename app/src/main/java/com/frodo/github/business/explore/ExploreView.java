package com.frodo.github.business.explore;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.UIView;
import com.frodo.github.R;
import com.frodo.github.bean.ShowCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frodo on 2016/4/30.
 */
public class ExploreView extends UIView {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private List<View> pager = new ArrayList<>();

    public ExploreView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container, int layoutResId) {
        super(presenter, inflater, container, layoutResId);
    }

    @Override
    public void initView() {
        viewPager = (ViewPager) getRootView().findViewById(R.id.showcases_vp);
        pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return pager.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view.equals(object);
            }

            @Override
            public void destroyItem(ViewGroup view, int position, Object object) {
                view.removeView(pager.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup view, int position) {
                view.addView(pager.get(position));
                return pager.get(position);
            }
        };
        viewPager.setAdapter(pagerAdapter);
    }

    @Override
    public void registerListener() {
    }

    public void showShowCaseList(List<ShowCase> showCases) {
        if (showCases!=null && !showCases.isEmpty()){
            for (ShowCase showcase: showCases) {
                View itemView = LayoutInflater.from(getRootView().getContext()).inflate(R.layout.view_showcases_viewpager_item, null);
                ImageView imageView = (ImageView) itemView.findViewById(R.id.img_iv);
                TextView textView = (TextView) itemView.findViewById(R.id.text_tv);
                Glide.with(getPresenter().getAndroidContext())
                        .load(showcase.imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(imageView);
                textView.setText(showcase.name);

                pager.add(itemView);
            }
            pagerAdapter.notifyDataSetChanged();
        }
    }

    public void showError(String message) {
        if (isOnShown()) {
            Toast.makeText(getPresenter().getAndroidContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}

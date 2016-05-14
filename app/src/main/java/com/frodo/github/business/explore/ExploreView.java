package com.frodo.github.business.explore;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.UIView;
import com.frodo.app.android.core.toolbox.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.github.R;
import com.frodo.github.bean.Repository;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.business.repository.RepositoryFragment;
import com.frodo.github.business.showcases.ShowCaseDetailFragment;
import com.frodo.github.view.BaseListViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frodo on 2016/4/30.
 */
public class ExploreView extends UIView {

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private List<View> pager = new ArrayList<>();

    private ListView trendingRepositoriesLV;
    private Adapter repositoryAdapter;
    private View trendingRepositoriesHeaderView;
    private View trendingRepositoriesFooterView;

    public ExploreView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.fragment_explore);
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

        trendingRepositoriesLV = (ListView) getRootView().findViewById(R.id.trending_repositories_lv);
        trendingRepositoriesHeaderView = View.inflate(getRootView().getContext(), R.layout.view_repository_header, null);
        trendingRepositoriesFooterView = View.inflate(getRootView().getContext(), R.layout.view_footer, null);
        trendingRepositoriesLV.addHeaderView(trendingRepositoriesHeaderView);
        trendingRepositoriesLV.addFooterView(trendingRepositoriesFooterView);
        ((TextView) trendingRepositoriesFooterView.findViewById(R.id.text_tv)).setText("View more trending repositories");
        repositoryAdapter = new Adapter(getRootView().getContext());
        trendingRepositoriesLV.setAdapter(repositoryAdapter);
    }

    @Override
    public void registerListener() {
        trendingRepositoriesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle arguments = new Bundle();
                Repository repository = repositoryAdapter.getItem(position);
                if (repository != null) {
                    arguments.putString("repo", repository.name);
                    FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), RepositoryFragment.class, arguments);
                }
            }
        });
    }

    public void showShowCaseList(List<ShowCase> showCases) {
        if (showCases != null && !showCases.isEmpty()) {
            for (final ShowCase showcase : showCases) {
                View itemView = LayoutInflater.from(getRootView().getContext()).inflate(R.layout.view_showcases_viewpager_item, null);
                SimpleDraweeView imageView = (SimpleDraweeView) itemView.findViewById(R.id.img_iv);
                TextView textView = (TextView) itemView.findViewById(R.id.text_tv);

                imageView.setImageURI(Uri.parse(showcase.imageUrl));
                textView.setText(showcase.name);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle arguments = new Bundle();
                        arguments.putString("slug", showcase.slug);
                        FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), ShowCaseDetailFragment.class, arguments);
                    }
                });

                pager.add(itemView);
            }
            pagerAdapter.notifyDataSetChanged();
            viewPager.setVisibility(View.VISIBLE);
        } else {
            viewPager.setVisibility(View.INVISIBLE);
        }
    }

    public void showTrendingRepositoryList(List<Repository> repositories) {
        if (repositories != null && !repositories.isEmpty()) {
            repositoryAdapter.refreshObjects(repositories);
            repositoryAdapter.notifyDataSetChanged();
            ((View) trendingRepositoriesLV.getParent()).setVisibility(View.VISIBLE);
        } else {
            ((View) trendingRepositoriesLV.getParent()).setVisibility(View.INVISIBLE);
        }
    }

    public void showError(String message) {
        if (isOnShown()) {
            Toast.makeText(getPresenter().getAndroidContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private class Adapter extends BaseListViewAdapter<Repository> {
        public Adapter(Context context) {
            super(context, R.layout.view_repositories_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder vh;
            if (convertView == null) {
                convertView = inflateItemView();
                vh = new ViewHolder();
                vh.ownerHeadIV = (SimpleDraweeView) convertView.findViewById(R.id.owner_head_iv);
                vh.repoTV = (TextView) convertView.findViewById(R.id.repo_tv);
                vh.starCountTV = (TextView) convertView.findViewById(R.id.star_count_tv);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            final Repository bean = getItem(position);
            if (bean.owner != null && bean.owner.avatarUrl != null) {
                vh.ownerHeadIV.setImageURI(Uri.parse(bean.owner.avatarUrl));
            }

            vh.repoTV.setText(bean.full_name);
            vh.starCountTV.setText(String.valueOf(bean.stargazers_count));
            return convertView;
        }

        class ViewHolder {
            SimpleDraweeView ownerHeadIV;
            TextView repoTV;
            TextView starCountTV;
        }
    }
}

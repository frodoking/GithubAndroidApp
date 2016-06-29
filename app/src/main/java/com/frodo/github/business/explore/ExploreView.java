package com.frodo.github.business.explore;

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

import com.facebook.drawee.view.SimpleDraweeView;
import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.github.R;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.business.AbstractUIView;
import com.frodo.github.business.repository.RepositoriesForListViewAdapter;
import com.frodo.github.business.repository.RepositoryFragment;
import com.frodo.github.view.CardViewGroup;
import com.frodo.github.view.OcticonView;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frodo on 2016/4/30.
 */
public class ExploreView extends AbstractUIView {

	private ViewPager viewPager;
	private PagerAdapter pagerAdapter;
	private List<View> pager = new ArrayList<>();

	private CardViewGroup trendingRepositoriesCVG;
	private ListView trendingRepositoriesLV;
	private RepositoriesForListViewAdapter repositoryAdapter;

	public ExploreView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
		super(presenter, inflater, container, R.layout.uiview_explore);
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

		trendingRepositoriesCVG = (CardViewGroup) getRootView().findViewById(R.id.trending_repositories_cvg);
		trendingRepositoriesLV = (ListView) trendingRepositoriesCVG.getContentView();

		OcticonView titleOV = (OcticonView) trendingRepositoriesCVG.getHeaderView().findViewById(R.id.title_ov);
		titleOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_flame);
		titleOV.setText("Trending repositories");
		titleOV.setTextColorRes(android.R.color.black);

		OcticonView subtitleOV = (OcticonView) trendingRepositoriesCVG.getHeaderView().findViewById(R.id.subtitle_ov);
		subtitleOV.setText("this week");
		subtitleOV.setPaddingDp(0);

		((TextView) trendingRepositoriesCVG.getFooterView().findViewById(R.id.text_tv)).setText("View more trending repositories");
		repositoryAdapter = new RepositoriesForListViewAdapter(getRootView().getContext());
		trendingRepositoriesLV.setAdapter(repositoryAdapter);
	}

	@Override
	public void registerListener() {
		trendingRepositoriesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle arguments = new Bundle();
				Repo repository = repositoryAdapter.getItem(position);
				if (repository != null) {
					arguments.putString("repo", repository.owner.login + "/" + repository.name);
					FragmentScheduler.nextFragmentWithUniqueTag((FragmentContainerActivity) getPresenter().getAndroidContext(), RepositoryFragment.class, arguments);
				}
			}
		});
		trendingRepositoriesCVG.getFooterView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), TrendingFragment.class);
			}
		});
	}

	public void showShowCaseList(List<ShowCase> showCases) {
		if (showCases != null && !showCases.isEmpty()) {
			for (final ShowCase showcase : showCases) {
				View itemView = LayoutInflater.from(getRootView().getContext()).inflate(R.layout.view_showcases_viewpager_item, null);
				SimpleDraweeView imageView = (SimpleDraweeView) itemView.findViewById(R.id.img_iv);
				TextView textView = (TextView) itemView.findViewById(R.id.text_tv);

				imageView.setImageURI(Uri.parse(showcase.image_url));
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

	public void showTrendingRepositoryList(List<Repo> repositories) {
		if (repositories != null && !repositories.isEmpty()) {
			repositoryAdapter.refreshObjects(repositories);
			repositoryAdapter.notifyDataSetChanged();
			trendingRepositoriesCVG.setVisibility(View.VISIBLE);
		} else {
			trendingRepositoriesCVG.setVisibility(View.INVISIBLE);
		}
	}
}

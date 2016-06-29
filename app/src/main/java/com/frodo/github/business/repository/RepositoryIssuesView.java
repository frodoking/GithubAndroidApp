package com.frodo.github.business.repository;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Issue;
import com.frodo.github.business.AbstractUIView;

import java.util.List;

/**
 * Created by frodo on 16/6/11.
 */
public class RepositoryIssuesView extends AbstractUIView {

	private RadioGroup radioGroup;
	private ListView listView;
	private IssuesForListViewAdapter adapter;

	public RepositoryIssuesView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
		super(presenter, inflater, container, R.layout.uiview_repository_issues);
	}

	@Override
	public void initView() {
		radioGroup = (RadioGroup) getRootView().findViewById(R.id.filter_rg);
		listView = (ListView) getRootView().findViewById(R.id.lv);
		adapter = new IssuesForListViewAdapter(getPresenter().getAndroidContext());
		listView.setAdapter(adapter);
	}

	@Override
	public void registerListener() {
		radioGroup.check(R.id.filter_open_rb);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
					case R.id.filter_open_rb:
						((RepositoryIssuesFragment) getPresenter()).loadOpenIssuesWithReactor();
						break;
					case R.id.filter_closed_rb:
						((RepositoryIssuesFragment) getPresenter()).loadClosedIssuesWithReactor();
						break;
					case R.id.filter_yours_rb:
						((RepositoryIssuesFragment) getPresenter()).loadYoursIssuesWithReactor();
						break;
					default:
						break;
				}
			}
		});
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Bundle arguments = new Bundle();
				arguments.putParcelable("issue", (Parcelable) parent.getItemAtPosition(position));
				FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), CommentsFragment.class, arguments);
			}
		});
	}

	public void showDetail(List<Issue> issues) {
		adapter.refreshObjects(issues);
	}

	@Override
	public void onShowOrHide(boolean isShown) {
		getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
	}
}

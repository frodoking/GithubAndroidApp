package com.frodo.github.business.repository;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Content;
import com.frodo.github.business.AbstractUIView;
import com.frodo.github.view.BaseListViewAdapter;
import com.frodo.github.view.OcticonView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by frodo on 2016/6/7.
 */
public class RepositoryContentsDirectoryView extends AbstractUIView {

	private OcticonView branchsOV;
	private OcticonView commitsOV;
	private TextView pathTV;
	private ListView directoryLV;
	private BaseListViewAdapter adapter;

	public RepositoryContentsDirectoryView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
		super(presenter, inflater, container, R.layout.uiview_repository_contents_directory);
	}

	@Override
	public void initView() {
		branchsOV = (OcticonView) getRootView().findViewById(R.id.branchs_ov);
		commitsOV = (OcticonView) getRootView().findViewById(R.id.commits_ov);
		pathTV = (TextView) getRootView().findViewById(R.id.path_tv);
		directoryLV = (ListView) getRootView().findViewById(R.id.directory_lv);
	}

	@Override
	public void registerListener() {
		pathTV.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((RepositoryContentsDirectoryFragment) getPresenter()).backToParentDirectory();
			}
		});

		directoryLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Content content = (Content) parent.getAdapter().getItem(position);
				if (content.isDir()) {
					((RepositoryContentsDirectoryFragment) getPresenter()).loadContentsWithReactor(content.path, "master");
				} else if (content.isFile()) {
					Bundle arguments = new Bundle();
					arguments.putParcelable("content", content);
					FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), RepositoryContentsFileFragment.class, arguments);
				}
			}
		});
	}

	@Override
	public void onShowOrHide(boolean isShown) {
		getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
	}

	public void showSimple(String[] branchs, int commits, String path) {
		branchsOV.setText(Arrays.toString(branchs));
		commitsOV.setText(String.format("%scommits", commits));
		pathTV.setText(path);
	}

	public void showContents(List<Content> contents) {
		if (adapter == null) {
			adapter = new ContentsListViewAdapter(getPresenter().getAndroidContext());
			directoryLV.setAdapter(adapter);
		}

		if (contents == null || contents.isEmpty()) {
			directoryLV.setVisibility(View.GONE);
		} else {
			directoryLV.setVisibility(View.VISIBLE);
			adapter.refreshObjects(contents);
		}
	}
}

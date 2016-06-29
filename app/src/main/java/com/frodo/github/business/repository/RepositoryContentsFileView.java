package com.frodo.github.business.repository;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.github.R;
import com.frodo.github.business.AbstractUIView;
import com.frodo.github.view.OcticonView;
import com.mikepenz.octicons_typeface_library.Octicons;

import us.feras.mdv.MarkdownView;

/**
 * Created by frodo on 2016/6/8.
 */
public class RepositoryContentsFileView extends AbstractUIView {
	private OcticonView pathOV;
	private OcticonView commitOV;
	private MarkdownView contentsMDV;

	public RepositoryContentsFileView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
		super(presenter, inflater, container, R.layout.uiview_repository_contents_file);
	}

	@Override
	public void initView() {
		pathOV = (OcticonView) getRootView().findViewById(R.id.path_ll).findViewById(R.id.title_ov);
		commitOV = (OcticonView) getRootView().findViewById(R.id.path_ll).findViewById(R.id.subtitle_ov);
		pathOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_file_text);
		commitOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_git_commit);

		contentsMDV = (MarkdownView) getRootView().findViewById(R.id.contents_mdv);
	}

	@Override
	public void registerListener() {
	}

	@Override
	public void onShowOrHide(boolean isShown) {
		getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
	}

	public void showPath(String path) {
		pathOV.setText(path);
	}

	public void showContent(String text) {
		contentsMDV.loadMarkdown(text);
	}
}

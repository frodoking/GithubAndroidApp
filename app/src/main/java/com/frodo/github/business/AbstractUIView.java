package com.frodo.github.business;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.UIView;
import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.R;

/**
 * add empty function
 * Created by frodo on 2016/6/4.
 */
public abstract class AbstractUIView extends UIView {
	private FrameLayout rootView;
	private View contentView;
	private View errorView;

	public AbstractUIView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container, int layoutResId) {
		super(presenter, inflater, container, layoutResId);
		rootView = new FrameLayout(presenter.getAndroidContext());
		this.contentView = inflater.inflate(layoutResId, container, false);
		this.errorView = inflater.inflate(R.layout.view_error, container, false);
		rootView.addView(contentView,
				new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						FrameLayout.LayoutParams.MATCH_PARENT));
		rootView.addView(errorView,
				new FrameLayout.LayoutParams(
						FrameLayout.LayoutParams.MATCH_PARENT,
						FrameLayout.LayoutParams.MATCH_PARENT));
		hideEmptyView();
	}

	@Override
	public View getRootView() {
		return this.rootView;
	}

	public void reload() {
		if (getPresenter() != null && getPresenter() instanceof StatedFragment) {
			((StatedFragment) getPresenter()).onFirstTimeLaunched();
		}
	}

	public void showEmptyView() {
		showErrorView("");
	}

	public void hideEmptyView() {
		hideErrorView();
	}

	public void showErrorView(String errorMsg) {
		contentView.setVisibility(View.GONE);
		errorView.setVisibility(View.VISIBLE);
		((TextView) errorView.findViewById(R.id.error_tv)).setText(errorMsg);
		errorView.findViewById(R.id.logo_fiiv).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				reload();
			}
		});
	}

	public void hideErrorView() {
		errorView.setVisibility(View.GONE);
		contentView.setVisibility(View.VISIBLE);
		errorView.findViewById(R.id.logo_fiiv).setOnClickListener(null);
	}
}

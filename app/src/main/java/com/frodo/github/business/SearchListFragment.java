package com.frodo.github.business;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.app.framework.controller.IModel;
import com.frodo.github.view.BaseRecyclerViewAdapter;

import java.util.ArrayList;

/**
 * Created by frodo on 2016/5/31.
 */
public abstract class SearchListFragment<M extends IModel, Bean extends Parcelable> extends StatedFragment<SearchUIListView, M> {

	private static final String STATE_KEY = "state";
	private ArrayList<Bean> stateBeans;

	@Override
	public SearchUIListView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
		return new SearchUIListView<Bean>(this, inflater, container) {

			@Override
			public BaseRecyclerViewAdapter adapter() {
				return uiViewAdapter();
			}
		};
	}

	@Override
	public void onSaveState(Bundle outState) {
		outState.putParcelableArrayList(STATE_KEY, stateBeans);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {
		stateBeans = savedInstanceState.getParcelableArrayList(STATE_KEY);
		getUIView().showList(stateBeans);
	}


	public void setStateBeans(ArrayList<Bean> stateBeans) {
		this.stateBeans = stateBeans;
	}

	public abstract BaseRecyclerViewAdapter uiViewAdapter();

	public abstract void doSearch(String searchKey);
}

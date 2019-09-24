package com.frodo.github.business.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.dto.response.GithubEvent;
import com.frodo.github.view.CircleProgressDialog;
import com.frodo.github.view.ViewProvider;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frodo on 16/6/10.
 */
public class EventsFragment extends StatedFragment<EventsView, EventsModel> {

	private List<GithubEvent> stateContents;
	private String repoOwner;
	private String repo;

	private String title = "Events";

	@Override
	public EventsView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
		return new EventsView(this, inflater, container);
	}

	@Override
	public void onSaveState(Bundle outState) {
		outState.putParcelableArrayList("contentsState", (ArrayList<? extends Parcelable>) stateContents);
	}

	@Override
	public void onRestoreState(Bundle savedInstanceState) {
		stateContents = savedInstanceState.getParcelableArrayList("contentsState");
		getUIView().showDetail(stateContents);
	}

	@Override
	public void onResume() {
		super.onResume();
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
	}

	@SuppressLint ("CheckResult")
	@Override
	public void onFirstTimeLaunched() {
		Bundle bundle = getArguments();
		//TODO events_account_{username}
		// events_user_{username}
		// events_repo_{owner}_{repo}
		if (bundle != null && bundle.containsKey("events_args")) {
			String[] argsArray = bundle.getString("events_args").split("_");

			if (argsArray[0].equalsIgnoreCase("events")) {
				Observable<List<GithubEvent>> observable = null;
				if (argsArray[1].equalsIgnoreCase("account")) {
					observable = getModel().loadAccountEvents(argsArray[2]);
				} else if (argsArray[1].equalsIgnoreCase("user")) {
					title = "News";
					observable = getModel().loadReceivedEvents(argsArray[2]);
				} else if (argsArray[1].equalsIgnoreCase("repo")) {
					repoOwner = argsArray[2];
					repo = argsArray[3];
				}

				if (observable != null) {
					observable.subscribeOn(Schedulers.io())
							.doOnSubscribe(new Consumer<Disposable>() {
								@Override public void accept(Disposable disposable) throws Exception
								{
									getUIView().showEmptyView();
									CircleProgressDialog.showLoadingDialog(getAndroidContext());
								}
							})
							.observeOn(AndroidSchedulers.mainThread())
							.subscribe(new Consumer<List<GithubEvent>>() {
										   @Override public void accept(List<GithubEvent> events)
										   {
											   stateContents = events;
											   CircleProgressDialog.hideLoadingDialog();
											   getUIView().hideEmptyView();
											   getUIView().showDetail(events);
										   }
							           },
									new Consumer<Throwable>() {
										@Override public void accept(Throwable throwable)
										{
											CircleProgressDialog.hideLoadingDialog();
											getUIView().showErrorView(ViewProvider.handleError(getMainController().getConfig().isDebug(), throwable));
										}
									});
				}
			}
		}
	}
}

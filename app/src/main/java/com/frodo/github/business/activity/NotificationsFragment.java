package com.frodo.github.business.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.dto.response.Notification;
import com.frodo.github.view.CircleProgressDialog;
import com.frodo.github.view.ViewProvider;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 16/6/10.
 */
public class NotificationsFragment extends StatedFragment<NotificationsView, NotificationsModel> {
	private List<Notification> stateContents;

	@Override
	public NotificationsView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
		return new NotificationsView(this, inflater, container);
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
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Notifications");
	}

	@Override
	public void onFirstTimeLaunched() {
		getModel().loadNotifications().subscribeOn(Schedulers.io())
				.doOnSubscribe(new Action0() {
					@Override
					public void call() {
						getUIView().showEmptyView();
						CircleProgressDialog.showLoadingDialog(getAndroidContext());
					}
				})
				.subscribeOn(AndroidSchedulers.mainThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Action1<List<Notification>>() {
					           @Override
					           public void call(List<Notification> notifications) {
						           stateContents = notifications;
						           CircleProgressDialog.hideLoadingDialog();
						           getUIView().hideEmptyView();
						           getUIView().showDetail(notifications);
					           }
				           },
						new Action1<Throwable>() {
							@Override
							public void call(Throwable throwable) {
								CircleProgressDialog.hideLoadingDialog();
								getUIView().showErrorView(ViewProvider.handleError(getMainController().getConfig().isDebug(), throwable));
							}
						});
	}
}

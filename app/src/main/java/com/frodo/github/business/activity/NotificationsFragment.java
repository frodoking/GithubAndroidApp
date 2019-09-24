package com.frodo.github.business.activity;

import android.annotation.SuppressLint;
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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frodo on 16/6/10.
 */
public class NotificationsFragment extends StatedFragment<NotificationsView, NotificationsModel>
{
    private List<Notification> stateContents;

    @Override public NotificationsView createUIView(Context context, LayoutInflater inflater, ViewGroup container)
    {
        return new NotificationsView(this, inflater, container);
    }

    @Override public void onSaveState(Bundle outState)
    {
        outState.putParcelableArrayList("contentsState", (ArrayList<? extends Parcelable>) stateContents);
    }

    @Override public void onRestoreState(Bundle savedInstanceState)
    {
        stateContents = savedInstanceState.getParcelableArrayList("contentsState");
        getUIView().showDetail(stateContents);
    }

    @Override public void onResume()
    {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Notifications");
    }

    @SuppressLint ("CheckResult")
	@Override
	public void onFirstTimeLaunched()
    {
        getModel().loadNotifications().subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>()
        {
            @Override public void accept(Disposable disposable)
            {
                getUIView().showEmptyView();
                CircleProgressDialog.showLoadingDialog(getAndroidContext());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Notification>>()
        {
            @Override public void accept(List<Notification> notifications)
            {
                stateContents = notifications;
                CircleProgressDialog.hideLoadingDialog();
                getUIView().hideEmptyView();
                getUIView().showDetail(notifications);
            }
        }, new Consumer<Throwable>()
        {
            @Override public void accept(Throwable throwable)
            {
                CircleProgressDialog.hideLoadingDialog();
                getUIView()
                        .showErrorView(ViewProvider.handleError(getMainController().getConfig().isDebug(), throwable));
            }
        });
    }
}

package com.frodo.github.business.activity;

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

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 16/6/10.
 */
public class EventsFragment extends StatedFragment<EventsView, EventsModel> {

    private List<GithubEvent> stateContents;

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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Events");
    }

    @Override
    public void onFirstTimeLaunched() {
        Bundle bundle = getArguments();
        if (bundle.containsKey("username")) {
            String username = bundle.getString("username");
            getModel().loadReceivedEvents(username).subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            getUIView().showEmptyView();
                            CircleProgressDialog.showLoadingDialog(getAndroidContext());
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<GithubEvent>>() {
                                   @Override
                                   public void call(List<GithubEvent> events) {
                                       stateContents = events;
                                       CircleProgressDialog.hideLoadingDialog();
                                       getUIView().hideEmptyView();
                                       getUIView().showDetail(events);
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
}

package com.frodo.github.business.explore;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.view.CircleProgressDialog;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/4/30.
 */
public class ExploreFragment extends StatedFragment<ExploreView, ExploreModel> {

    private ArrayList<ShowCase> showCases;
    private ArrayList<Repo> repositories;

    @Override
    public ExploreView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new ExploreView(this, inflater, container);
    }

    @Override
    public void onFirstTimeLaunched() {
        loadDataWithReactor();
    }

    @Override
    public void onSaveState(Bundle outState) {
        if (this.showCases != null) {
            outState.putParcelableArrayList("showCases", this.showCases);
        }
        if (this.repositories != null) {
            outState.putParcelableArrayList("repositories", this.repositories);
        }
    }

    @Override
    public void onRestoreState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey("showCases") && savedInstanceState.containsKey("repositories")) {
            this.showCases = savedInstanceState.getParcelableArrayList("showCases");
            this.repositories = savedInstanceState.getParcelableArrayList("repositories");
            getUIView().showShowCaseList(showCases);
            getUIView().showTrendingRepositoryList(repositories);
        } else {
            loadDataWithReactor();
        }
    }

    private void loadDataWithReactor() {
        final Observable<List<ShowCase>> showCaseObservable = getModel().loadShowCasesWithReactor();
        final Observable<List<Repo>> repositoryObservable = getModel().loadRepositoriesWithReactor();

        Observable.merge(showCaseObservable, repositoryObservable)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        CircleProgressDialog.showLoadingDialog(getAndroidContext());
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<? extends Parcelable>>() {
                            @Override
                            public void call(List<? extends Parcelable> parcelables) {
                                showCases = (ArrayList<ShowCase>) parcelables.get(0);
                                repositories = (ArrayList<Repo>) parcelables.get(1);

                                getUIView().showShowCaseList(showCases);
                                getUIView().showTrendingRepositoryList(repositories);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                if (getModel().isEnableCached()) {
                                    List<ShowCase> showCases = getModel().getShowCasesFromCache();
                                    if (showCases != null) {
                                        getUIView().showShowCaseList(showCases);
                                    }
                                }
                            }
                        },
                        new Action0() {
                            @Override
                            public void call() {
                                CircleProgressDialog.hideLoadingDialog();
                            }
                        }
                );
    }
}

package com.frodo.github.business.explore;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.view.CircleProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/4/30.
 */
public class ExploreFragment extends StatedFragment<ExploreView, ExploreModel> {

    private static final String STATE_SHOWCASE = "state_showcase";
    private static final String STATE_REPOSITORIES = "state_repositories";
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
            outState.putParcelableArrayList(STATE_SHOWCASE, this.showCases);
        }
        if (this.repositories != null) {
            outState.putParcelableArrayList(STATE_REPOSITORIES, this.repositories);
        }
    }

    @Override
    public void onRestoreState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(STATE_SHOWCASE) && savedInstanceState.containsKey(STATE_REPOSITORIES)) {
            this.showCases = savedInstanceState.getParcelableArrayList(STATE_SHOWCASE);
            this.repositories = savedInstanceState.getParcelableArrayList(STATE_REPOSITORIES);
            getUIView().showShowCaseList(showCases);
            getUIView().showTrendingRepositoryList(repositories);
        } else {
            loadDataWithReactor();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tag());
    }

    @Override
    public String tag() {
        return "Explore";
    }

    private void loadDataWithReactor() {
        final Observable<List<ShowCase>> showCaseObservable = getModel().loadShowCasesWithReactor();
        final Observable<List<Repo>> repositoryObservable = getModel().loadRepositoriesWithReactor();

        Observable.combineLatest(showCaseObservable, repositoryObservable, new Func2<List<ShowCase>, List<Repo>, Map<String, Object>>() {
            @Override
            public Map<String, Object> call(List<ShowCase> showCases, List<Repo> repositories) {
                Map<String, Object> map = new HashMap<>(2);
                map.put(STATE_SHOWCASE, showCases);
                map.put(STATE_REPOSITORIES, repositories);
                return map;
            }
        }).doOnSubscribe(new Action0() {
            @Override
            public void call() {
                getUIView().showEmptyView();
                CircleProgressDialog.showLoadingDialog(getAndroidContext());
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Map<String, Object>>() {
                            @Override
                            public void call(Map<String, Object> result) {
                                CircleProgressDialog.hideLoadingDialog();

                                ArrayList<ShowCase> showCases = (ArrayList<ShowCase>) result.get(STATE_SHOWCASE);
                                ArrayList<Repo> repositories = (ArrayList<Repo>) result.get(STATE_REPOSITORIES);

                                ExploreFragment.this.showCases = showCases;
                                ExploreFragment.this.repositories = repositories;

                                getUIView().hideEmptyView();
                                getUIView().showShowCaseList(showCases);
                                getUIView().showTrendingRepositoryList(repositories);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                CircleProgressDialog.hideLoadingDialog();
                                if (getModel().isEnableCached()) {
                                    List<ShowCase> showCases = getModel().getShowCasesFromCache();
                                    if (showCases != null) {
                                        getUIView().showShowCaseList(showCases);
                                    }
                                }
                                getUIView().showErrorView(throwable);
                            }
                        });
    }
}

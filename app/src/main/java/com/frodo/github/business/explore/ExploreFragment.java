package com.frodo.github.business.explore;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.Repository;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.view.CircleProgressDialog;

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
    private static final String KEY_CACHE_SHOWCASES = "showcases_cache";

    @Override
    public ExploreView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new ExploreView(this, inflater, container);
    }

    @Override
    public ExploreModel createModel() {
        return new ExploreModel(getMainController());
    }

    @Override
    protected void onFirstTimeLaunched() {
        loadDataWithReactor();
    }

    @Override
    protected void onSaveState(Bundle outState) {
        List<ShowCase> showCases = getModel().getShowCases();
        try {
            outState.putString(KEY_CACHE_SHOWCASES, new ObjectMapper().writeValueAsString(showCases));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String moviesJson = savedInstanceState.getString(KEY_CACHE_SHOWCASES);
            List<ShowCase> showCases = JsonConverter.convert(moviesJson, new TypeReference<List<ShowCase>>() {
            });
            getUIView().showShowCaseList(showCases);
        }
    }

    private void loadDataWithReactor() {
        final Observable<List<ShowCase>> showCaseObservable = getModel().loadShowCasesWithReactor();
        final Observable<List<Repository>> repositoryObservable = getModel().loadRepositoriesWithReactor();

        Observable.combineLatest(showCaseObservable, repositoryObservable, new Func2<List<ShowCase>, List<Repository>, Map<String, Object>>() {
            @Override
            public Map<String, Object> call(List<ShowCase> showCases, List<Repository> repositories) {
                Map<String, Object> map = new HashMap<>(2);
                map.put("showCases", showCases);
                map.put("repositories", repositories);
                return map;
            }
        })
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
                        new Action1<Map<String, Object>>() {
                            @Override
                            public void call(Map<String, Object> result) {
                                List<ShowCase> showCases = (List<ShowCase>) result.get("showCases");
                                List<Repository> repositories = (List<Repository>) result.get("repositories");
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
                                        return;
                                    }
                                }
                                throwable.printStackTrace();
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

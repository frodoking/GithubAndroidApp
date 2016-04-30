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
import com.frodo.app.framework.config.Environment;
import com.frodo.github.R;
import com.frodo.github.bean.ShowCase;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/4/30.
 */
public class ExploreFragment extends StatedFragment<ExploreView, ExploreModel> {
    private static final String KEY_CACHE_SHOWCASES = "showcases_cache";

    @Override
    public ExploreView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new ExploreView(this, inflater, container, R.layout.fragment_explore);
    }

    @Override
    public ExploreModel createModel() {
        return new ExploreModel(getMainController());
    }

    @Override
    protected void onFirstTimeLaunched() {
        loadShowCasesWithReactor();
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

    private void loadShowCasesWithReactor() {
        getModel().setEnableCached(true);

        Observable
        .create(new Observable.OnSubscribe<List<ShowCase>>() {
            @Override
            public void call(Subscriber<? super List<ShowCase>> subscriber) {
                getModel().loadShowCasesWithReactor(subscriber);
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                new Action1<List<ShowCase>>() {
                    @Override
                    public void call(List<ShowCase> result) {
                        getUIView().showShowCaseList(result);
                        getModel().setShowCases(result);
                    }
                },
                new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (getModel().isEnableCached()) {
                            List<ShowCase> movies = getModel().getShowCasesFromCache();
                            if (movies != null) {
                                getUIView().showShowCaseList(movies);
                                return;
                            }
                        }
                        getUIView().showError(throwable.getMessage());
                    }
                }
        );
    }
}

package com.frodo.github.business.repository;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.Repository;
import com.frodo.github.bean.ShowCase;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/5/7.
 */
public class RepositoryFragment extends StatedFragment<RepositoryView, RepositoryModel> {
    @Override
    public RepositoryView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new RepositoryView(this, inflater, container);
    }

    @Override
    protected RepositoryModel createModel() {
        return new RepositoryModel(getMainController());
    }

    @Override
    protected void onFirstTimeLaunched() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("repo")) {
            loadRepositoryWithReactor(bundle.getString("slug"));
        }
    }

    private void loadRepositoryWithReactor(final String slug) {
        Observable
                .create(new Observable.OnSubscribe<Repository>() {
                    @Override
                    public void call(Subscriber<? super Repository> subscriber) {
                        getModel().loadRepositoryDetailWithReactor(slug, subscriber);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Repository>() {
                            @Override
                            public void call(Repository result) {
                                getUIView().showDetail(result);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                getUIView().showError(throwable.getMessage());
                            }
                        }
                );
    }
}

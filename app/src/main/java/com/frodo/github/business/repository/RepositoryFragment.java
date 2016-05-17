package com.frodo.github.business.repository;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.bean.Repository;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/5/7.
 */
public class RepositoryFragment extends StatedFragment<RepositoryView, RepositoryModel> {
    private String repo;

    @Override
    public RepositoryView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new RepositoryView(this, inflater, container);
    }

    @Override
    protected void onFirstTimeLaunched() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("repo")) {
            repo = bundle.getString("repo");
            loadRepositoryWithReactor(repo);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tag());
    }

    @Override
    public String tag() {
        return TextUtils.isEmpty(repo) ? "Repository" : repo;
    }

    private void loadRepositoryWithReactor(final String repo) {
        Observable
                .create(new Observable.OnSubscribe<Repository>() {
                    @Override
                    public void call(Subscriber<? super Repository> subscriber) {
                        getModel().loadRepositoryDetailWithReactor(repo, subscriber);
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

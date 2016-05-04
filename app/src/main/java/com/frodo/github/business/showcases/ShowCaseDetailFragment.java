package com.frodo.github.business.showcases;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.ShowCase;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/5/3.
 */
public class ShowCaseDetailFragment extends StatedFragment<ShowCaseDetailView, ShowCaseDetailModel> {
    @Override
    public ShowCaseDetailView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new ShowCaseDetailView(this, inflater, container);
    }

    @Override
    public ShowCaseDetailModel createModel() {
        return new ShowCaseDetailModel(getMainController());
    }

    @Override
    protected void onFirstTimeLaunched() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("slug")) {
            loadShowCasesWithReactor(bundle.getString("slug"));
        }
    }

    private void loadShowCasesWithReactor(final String slug) {
        Observable
                .create(new Observable.OnSubscribe<ShowCase>() {
                    @Override
                    public void call(Subscriber<? super ShowCase> subscriber) {
                        getModel().loadShowCaseDetailWithReactor(slug, subscriber);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<ShowCase>() {
                            @Override
                            public void call(ShowCase result) {
                                getUIView().showShowCaseDetail(result);
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

package com.frodo.github.business.showcases;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.view.CircleProgressDialog;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/5/3.
 */
public class ShowCaseDetailFragment extends StatedFragment<ShowCaseDetailView, ShowCaseDetailModel> {
    private String tag = "";

    @Override
    public ShowCaseDetailView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new ShowCaseDetailView(this, inflater, container);
    }

    @Override
    protected void onFirstTimeLaunched() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("slug")) {
            tag = bundle.getString("slug");
            loadShowCasesWithReactor(tag);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tag());
    }

    @Override
    public String tag() {
        return "Explore/" + tag;
    }

    private void loadShowCasesWithReactor(final String slug) {
        getModel().loadShowCaseDetailWithReactor(slug)
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
                        new Action1<ShowCase>() {
                            @Override
                            public void call(ShowCase result) {
                                getUIView().showShowCaseDetail(result);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }, new Action0() {
                            @Override
                            public void call() {
                                CircleProgressDialog.hideLoadingDialog();
                            }
                        }
                );
    }
}

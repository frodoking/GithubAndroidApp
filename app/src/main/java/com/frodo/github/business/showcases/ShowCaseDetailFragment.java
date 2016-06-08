package com.frodo.github.business.showcases;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.view.CircleProgressDialog;
import com.frodo.github.view.ViewProvider;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/5/3.
 */
public class ShowCaseDetailFragment extends StatedFragment<ShowCaseDetailView, ShowCaseDetailModel> {
    private String tag = "";
    private ShowCase showCase;

    @Override
    public ShowCaseDetailView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new ShowCaseDetailView(this, inflater, container);
    }

    @Override
    public void onFirstTimeLaunched() {
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
    public void onSaveState(Bundle outState) {
        outState.putParcelable("showcase", showCase);
    }

    @Override
    public void onRestoreState(Bundle savedInstanceState) {
        showCase = savedInstanceState.getParcelable("showcase");
        getUIView().showShowCaseDetail(showCase);
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
                        getUIView().showEmptyView();
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
                                CircleProgressDialog.hideLoadingDialog();
                                ShowCaseDetailFragment.this.showCase = result;
                                getUIView().hideEmptyView();
                                getUIView().showShowCaseDetail(result);
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

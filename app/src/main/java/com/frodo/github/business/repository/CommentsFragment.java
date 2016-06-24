package com.frodo.github.business.repository;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.dto.response.GithubComment;
import com.frodo.github.bean.dto.response.Issue;
import com.frodo.github.view.CircleProgressDialog;
import com.frodo.github.view.ViewProvider;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/6/24.
 */

public class CommentsFragment extends StatedFragment<CommentsView, RepositoryModel> {
    @Override
    public CommentsView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new CommentsView(this, inflater, container);
    }

    @Override
    public void onFirstTimeLaunched() {
        super.onFirstTimeLaunched();
        Bundle bundle = getArguments();
        if (bundle.containsKey("issue")) {
            final Issue issue = bundle.getParcelable("issue");

            getModel().listComments("frodoking", "App-Architecture", 14).subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            getUIView().showEmptyView();
                            CircleProgressDialog.showLoadingDialog(getAndroidContext());
                        }
                    })
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<GithubComment>>() {
                                   @Override
                                   public void call(List<GithubComment> comments) {
                                       CircleProgressDialog.hideLoadingDialog();
                                       getUIView().hideEmptyView();
                                       getUIView().showComments(issue, comments);
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

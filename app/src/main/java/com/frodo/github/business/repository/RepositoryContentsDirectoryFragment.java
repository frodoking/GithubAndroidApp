package com.frodo.github.business.repository;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.bean.dto.response.Content;
import com.frodo.github.view.CircleProgressDialog;
import com.frodo.github.view.ViewProvider;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/6/7.
 */
public class RepositoryContentsDirectoryFragment extends StatedFragment<RepositoryContentsDirectoryView, RepositoryModel> {
    private String repoName;
    private String path;
    private String branch;
    private List<Content> stateContents;

    @Override
    public RepositoryContentsDirectoryView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new RepositoryContentsDirectoryView(this, inflater, container);
    }

    @Override
    public void onFirstTimeLaunched() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("repo")) {
            repoName = bundle.getString("repo");
            if (repoName != null && repoName.contains("/")) {
                getUIView().showSimple(new String[]{"master"}, 1234, repoName);
                loadContentsWithReactor("", "master");
            }
        }
    }

    @Override
    public void onSaveState(Bundle outState) {
        outState.putParcelableArrayList("contentsState", (ArrayList<? extends Parcelable>) stateContents);
    }

    @Override
    public void onRestoreState(Bundle savedInstanceState) {
        stateContents = savedInstanceState.getParcelableArrayList("contentsState");
        getUIView().showContents(stateContents);
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tag());
    }

    @Override
    public String tag() {
        return TextUtils.isEmpty(repoName) ? "Repository" : repoName;
    }


    public void backToParentDirectory() {
        if (path.contains("/")) {
            loadContentsWithReactor(path.substring(0, path.lastIndexOf("/")), this.branch);
        }
    }

    public void loadContentsWithReactor(final String path, final String ref) {
        this.path = path;
        this.branch = ref;
        String[] strings = repoName.split("/");
        getModel().loadContentsWithReactor(strings[0], strings[1], path, ref)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        CircleProgressDialog.showLoadingDialog(getAndroidContext());
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Content>>() {
                               @Override
                               public void call(List<Content> contents) {
                                   getUIView().hideEmptyView();
                                   getUIView().showContents(contents);
                                   CircleProgressDialog.hideLoadingDialog();
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

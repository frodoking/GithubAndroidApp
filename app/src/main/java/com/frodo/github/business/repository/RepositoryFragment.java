package com.frodo.github.business.repository;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.view.CircleProgressDialog;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/5/7.
 */
public class RepositoryFragment extends StatedFragment<RepositoryView, RepositoryModel> {
    private String repoName;
    private Repo repo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public RepositoryView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new RepositoryView(this, inflater, container);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_repo, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFirstTimeLaunched() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("repo")) {
            repoName = bundle.getString("repo");
            loadRepositoryWithReactor(repoName);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tag());
    }

    @Override
    public void onSaveState(Bundle outState) {
        outState.putParcelable("repo", repo);
    }

    @Override
    public void onRestoreState(Bundle savedInstanceState) {
        repo = savedInstanceState.getParcelable("repo");
        getUIView().showDetail(repo);
    }

    @Override
    public String tag() {
        return TextUtils.isEmpty(repoName) ? "Repository" : repoName;
    }

    private void loadRepositoryWithReactor(final String repo) {
        getModel().loadRepositoryDetailWithReactor(repo)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        CircleProgressDialog.showLoadingDialog(getAndroidContext());
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Repo>() {
                               @Override
                               public void call(Repo repository) {
                                   RepositoryFragment.this.repo = repository;
                                   getUIView().showDetail(repository);
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
                        });

    }
}

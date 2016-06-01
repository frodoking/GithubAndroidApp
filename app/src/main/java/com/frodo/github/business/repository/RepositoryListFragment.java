package com.frodo.github.business.repository;

import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.business.SearchListFragment;
import com.frodo.github.view.BaseRecyclerViewAdapter;
import com.frodo.github.view.CircleProgressDialog;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/6/1.
 */
public class RepositoryListFragment extends SearchListFragment<RepositoryModel, Repo> {

    @Override
    protected RepositoryModel createModel() {
        return getMainController().getModelFactory().getOrCreateIfAbsent(RepositoryModel.TAG, RepositoryModel.class, getMainController());
    }

    @Override
    public BaseRecyclerViewAdapter uiViewAdapter() {
        return new RepositoriesAdapter(getAndroidContext());
    }

    @Override
    public void doSearch(String searchKey) {

    }

    @Override
    public void onFirstTimeLaunched() {
        getModel().loadUsersRepos("frodoking").subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        CircleProgressDialog.showLoadingDialog(getAndroidContext());
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Repo>>() {
                               @Override
                               public void call(List<Repo> repos) {
                                   CircleProgressDialog.hideLoadingDialog();
                                   setStateBeans((ArrayList<Repo>) repos);
                                   getUIView().showList(repos);
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                CircleProgressDialog.hideLoadingDialog();
                                throwable.printStackTrace();
                            }
                        });
    }
}

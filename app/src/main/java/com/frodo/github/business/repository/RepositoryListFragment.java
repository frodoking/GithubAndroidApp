package com.frodo.github.business.repository;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.business.SearchListFragment;
import com.frodo.github.view.BaseRecyclerViewAdapter;
import com.frodo.github.view.CircleProgressDialog;
import com.frodo.github.view.ViewProvider;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
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
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tag());
    }

    @Override
    public String tag() {
        return "Repositories";
    }

    @Override
    public BaseRecyclerViewAdapter uiViewAdapter() {
        return new RepositoriesAdapter(getAndroidContext(), true);
    }

    @Override
    public void doSearch(String searchKey) {

    }

    @Override
    public void onFirstTimeLaunched() {
        Bundle bundle = getArguments();
        Observable<List<Repo>> observable = null;
        //TODO repos_user_{username}
        if (bundle != null && bundle.containsKey("repos_args")) {
            String[] argsArray = bundle.getString("repos_args").split("_");

            if (argsArray.length == 3 && argsArray[0].equalsIgnoreCase("repos")) {
                if (argsArray[1].equalsIgnoreCase("user")) {
                    observable = getModel().loadUsersRepos(argsArray[2]);
                } else if (argsArray[1].equalsIgnoreCase("explore")) {
                    // do something
                }
            }
        }
        if (observable != null)
            observable.subscribeOn(Schedulers.io())
                    .doOnSubscribe(new Action0() {
                        @Override
                        public void call() {
                            getUIView().showEmptyView();
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
                                       getUIView().hideEmptyView();
                                       getUIView().showList(repos);
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

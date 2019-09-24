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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by frodo on 2016/6/1.
 */
public class RepositoryListFragment extends SearchListFragment<RepositoryModel, Repo>
{

    @Override protected RepositoryModel createModel()
    {
        return getMainController().getModelFactory()
                .getOrCreateIfAbsent(RepositoryModel.TAG, RepositoryModel.class, getMainController());
    }

    @Override public void onResume()
    {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tag());
    }

    @Override public String tag()
    {
        return "Repositories";
    }

    @Override public BaseRecyclerViewAdapter uiViewAdapter()
    {
        return new RepositoriesAdapter(getAndroidContext(), true);
    }

    @Override public void doSearch(String searchKey)
    {

    }

    @Override public void onFirstTimeLaunched()
    {
        Bundle bundle = getArguments();
        Observable<List<Repo>> observable = null;
        //TODO repos_user_{username}
        // repos_forks_{owner}_{repo}
        if (bundle != null && bundle.containsKey("repos_args"))
        {
            String[] argsArray = bundle.getString("repos_args").split("_");

            if (argsArray[0].equalsIgnoreCase("repos"))
            {
                if (argsArray[1].equalsIgnoreCase("user"))
                {
                    observable = getModel().loadUserRepositoriesWithReactor(argsArray[2]);
                }
                else if (argsArray[1].equalsIgnoreCase("explore"))
                {
                    // do something
                }
                else if (argsArray[1].equalsIgnoreCase("forks"))
                {
                    observable = getModel().loadRepositoryForksWithReactor(argsArray[2], argsArray[3]);
                }
            }
        }
        if (observable != null)
            observable.subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>()
            {
                @Override public void accept(Disposable disposable)
                {
                    getUIView().showEmptyView();
                    CircleProgressDialog.showLoadingDialog(getAndroidContext());
                }
            }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<List<Repo>>()
                    {
                        @Override public void accept(List<Repo> repos)
                        {
                            CircleProgressDialog.hideLoadingDialog();
                            setStateBeans((ArrayList<Repo>) repos);
                            getUIView().hideEmptyView();
                            getUIView().showList(repos);
                        }
                    }, new Consumer<Throwable>()
                    {
                        @Override public void accept(Throwable throwable)
                        {
                            CircleProgressDialog.hideLoadingDialog();
                            getUIView().showErrorView(
                                    ViewProvider.handleError(getMainController().getConfig().isDebug(), throwable));
                        }
                    });
    }
}

package com.frodo.github.business.user;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.frodo.github.bean.dto.response.User;
import com.frodo.github.business.SearchListFragment;
import com.frodo.github.view.BaseRecyclerViewAdapter;
import com.frodo.github.view.CircleProgressDialog;
import com.frodo.github.view.ViewProvider;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frodo on 2016/5/31.
 */
public class UserListFragment extends SearchListFragment<UserModel, User>
{

    @Override protected UserModel createModel()
    {
        return getMainController().getModelFactory()
                .getOrCreateIfAbsent(UserModel.TAG, UserModel.class, getMainController());
    }

    @Override public void onResume()
    {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tag());
    }

    @Override public String tag()
    {
        return "Developers";
    }

    @Override public BaseRecyclerViewAdapter uiViewAdapter()
    {
        return new DevelopersAdapter(getAndroidContext());
    }

    @Override public void doSearch(String searchKey)
    {
    }

    @Override public void onFirstTimeLaunched()
    {
        Bundle bundle = getArguments();
        Observable<List<User>> observable = null;
        //TODO users_user_followers_{username}
        // users_repo_stargazers_{owner}_{repo}
        if (bundle != null && bundle.containsKey("users_args"))
        {
            String[] argsArray = bundle.getString("users_args").split("_");

            if (argsArray[0].equalsIgnoreCase("users"))
            {
                if (argsArray[1].equalsIgnoreCase("user"))
                {
                    if (argsArray[2].equalsIgnoreCase("followers"))
                    {
                        observable = getModel().loadUserFollowers(argsArray[3]);
                    }
                    else if (argsArray[2].equalsIgnoreCase("following"))
                    {
                        observable = getModel().loadUserFollowing(argsArray[3]);
                    }
                }
                else if (argsArray[1].equalsIgnoreCase("repo"))
                {
                    if (argsArray[2].equalsIgnoreCase("stargazers"))
                    {
                        observable = getModel().loadRepoStargazers(argsArray[3], argsArray[4]);
                    }
                    else if (argsArray[2].equalsIgnoreCase("watchers"))
                    {
                        observable = getModel().loadRepoWatchers(argsArray[3], argsArray[4]);
                    }
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
            }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<User>>()
            {
                @Override public void accept(List<User> users)
                {
                    CircleProgressDialog.hideLoadingDialog();
                    setStateBeans((ArrayList<User>) users);
                    getUIView().hideEmptyView();
                    getUIView().showList(users);
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

package com.frodo.github.business.explore;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.business.ServerConfigurationModel;
import com.frodo.github.view.CircleProgressDialog;
import com.frodo.github.view.ViewProvider;
import com.mikepenz.octicons_typeface_library.Octicons;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.List;

/**
 * Created by frodo on 16/6/10.
 */
public class TrendingFragment extends StatedFragment<TrendingView, ExploreModel>
{
    public static final int TYPE_REPOS = 0;

    public static final int TYPE_DEVELOPERS = 1;

    private int currentType = TYPE_REPOS;

    private String currentLanguage = "";

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public TrendingView createUIView(Context context, LayoutInflater inflater, ViewGroup container)
    {
        return new TrendingView(this, inflater, container);
    }

    @Override public void onResume()
    {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Trending");
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_trending, menu);
        updateMenu(menu);
    }

    private void updateMenu(Menu menu)
    {
        ViewProvider.updateMenuItem(getAndroidContext(), menu, R.id.action_repositories, Octicons.Icon.oct_repo);
        ViewProvider.updateMenuItem(getAndroidContext(), menu, R.id.action_developers, Octicons.Icon.oct_mark_github);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_repositories:
                currentType = TYPE_REPOS;

                loadListBy(this.currentLanguage);
                break;
            case R.id.action_developers:
                currentType = TYPE_DEVELOPERS;

                loadListBy(this.currentLanguage);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onFirstTimeLaunched()
    {
        final ServerConfigurationModel serverConfigurationModel = getMainController().getModelFactory()
                .getOrCreateIfAbsent(ServerConfigurationModel.TAG, ServerConfigurationModel.class, getMainController());
        getUIView().loadLanguages(serverConfigurationModel.getLanguages());
    }

    public void loadListBy(String language)
    {
        this.currentLanguage = language;

        if (currentType == TYPE_REPOS)
        {
            loadTrendingRepositories();
        }
        else if (currentType == TYPE_DEVELOPERS)
        {
            loadTrendingDevelopers();
        }
    }

    private void loadTrendingRepositories()
    {
        getModel().loadTrendingRepositoriesWithReactor2("", this.currentLanguage)
                .doOnSubscribe(new Consumer<Disposable>()
                {
                    @Override public void accept(Disposable disposable)
                    {
                        getUIView().showEmptyView();
                        CircleProgressDialog.showLoadingDialog(getAndroidContext());
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<Repo>>()
        {
            @Override public void accept(List<Repo> repos)
            {
                CircleProgressDialog.hideLoadingDialog();
                getUIView().hideEmptyView();
                getUIView().showRepoList(repos);
            }
        }, new Consumer<Throwable>()
        {
            @Override public void accept(Throwable throwable)
            {
                CircleProgressDialog.hideLoadingDialog();
                getUIView()
                        .showErrorView(ViewProvider.handleError(getMainController().getConfig().isDebug(), throwable));
            }
        });
    }

    private void loadTrendingDevelopers()
    {
        getModel().loadTrendingDevelopersWithReactor("", this.currentLanguage).doOnSubscribe(new Consumer<Disposable>()
        {
            @Override public void accept(Disposable disposable)
            {
                getUIView().showEmptyView();
                CircleProgressDialog.showLoadingDialog(getAndroidContext());
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<User>>()
        {
            @Override public void accept(List<User> users)
            {
                CircleProgressDialog.hideLoadingDialog();
                getUIView().hideEmptyView();
                getUIView().showDeveloperList(users);
            }
        }, new Consumer<Throwable>()
        {
            @Override public void accept(Throwable throwable)
            {
                CircleProgressDialog.hideLoadingDialog();
                getUIView()
                        .showErrorView(ViewProvider.handleError(getMainController().getConfig().isDebug(), throwable));
            }
        });
    }

}

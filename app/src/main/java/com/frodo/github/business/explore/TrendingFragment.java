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

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 16/6/10.
 */
public class TrendingFragment extends StatedFragment<TrendingView, ExploreModel> {
    public static final int TYPE_REPOS = 0;
    public static final int TYPE_DEVELOPERS = 1;
    private int currentType = TYPE_REPOS;
    private String currentLanguage = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public TrendingView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new TrendingView(this, inflater, container);
    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Trending");
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_trending, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    @Override
    public void onFirstTimeLaunched() {
        final ServerConfigurationModel serverConfigurationModel = getMainController()
                .getModelFactory()
                .getOrCreateIfAbsent(ServerConfigurationModel.TAG, ServerConfigurationModel.class, getMainController());
        getUIView().loadLanguages(serverConfigurationModel.getLanguages());
    }

    public void loadListBy(String language) {
        this.currentLanguage = language;

        if (currentType == TYPE_REPOS) {
            loadTrendingRepositories();
        } else if (currentType == TYPE_DEVELOPERS) {
            loadTrendingDevelopers();
        }
    }

    private void loadTrendingRepositories() {
        getModel().loadTrendingRepositoriesWithReactor2("", this.currentLanguage)
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
                        new Action1<List<Repo>>() {
                            @Override
                            public void call(List<Repo> result) {
                                CircleProgressDialog.hideLoadingDialog();
                                getUIView().hideEmptyView();
                                getUIView().showRepoList(result);
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

    private void loadTrendingDevelopers() {
        getModel().loadTrendingDevelopersWithReactor("", this.currentLanguage)
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
                        new Action1<List<User>>() {
                            @Override
                            public void call(List<User> result) {
                                CircleProgressDialog.hideLoadingDialog();
                                getUIView().hideEmptyView();
                                getUIView().showDeveloperList(result);
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

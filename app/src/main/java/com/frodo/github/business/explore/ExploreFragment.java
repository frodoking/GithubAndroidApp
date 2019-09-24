package com.frodo.github.business.explore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.R;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.view.CircleProgressDialog;
import com.frodo.github.view.ViewProvider;
import com.mikepenz.octicons_typeface_library.Octicons;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by frodo on 2016/4/30.
 */
public class ExploreFragment extends StatedFragment<ExploreView, ExploreModel>
{

    private static final String STATE_SHOWCASE = "state_showcase";

    private static final String STATE_REPOSITORIES = "state_repositories";

    private ArrayList<ShowCase> showCases;

    private ArrayList<Repo> repositories;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public ExploreView createUIView(Context context, LayoutInflater inflater, ViewGroup container)
    {
        return new ExploreView(this, inflater, container);
    }

    @Override public void onFirstTimeLaunched()
    {
        loadDataWithReactor();
    }

    @Override public void onSaveState(Bundle outState)
    {
        if (this.showCases != null)
        {
            outState.putParcelableArrayList(STATE_SHOWCASE, this.showCases);
        }
        if (this.repositories != null)
        {
            outState.putParcelableArrayList(STATE_REPOSITORIES, this.repositories);
        }
    }

    @Override public void onRestoreState(Bundle savedInstanceState)
    {
        if (savedInstanceState.containsKey(STATE_SHOWCASE) && savedInstanceState.containsKey(STATE_REPOSITORIES))
        {
            this.showCases = savedInstanceState.getParcelableArrayList(STATE_SHOWCASE);
            this.repositories = savedInstanceState.getParcelableArrayList(STATE_REPOSITORIES);
            getUIView().showShowCaseList(showCases);
            getUIView().showTrendingRepositoryList(repositories);
        }
        else
        {
            loadDataWithReactor();
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tag());
    }

    @Override public String tag()
    {
        return "Explore";
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_explore, menu);
        updateMenu(menu);
    }

    private void updateMenu(Menu menu)
    {
        ViewProvider.updateMenuItem(getAndroidContext(), menu, R.id.action_trending, Octicons.Icon.oct_pulse);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_trending:
                FragmentScheduler.nextFragment(getAndroidContext(), TrendingFragment.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint ("CheckResult") private void loadDataWithReactor()
    {
        final Observable<List<ShowCase>> showCaseObservable = getModel().loadShowCasesWithReactor();
        final Observable<List<Repo>> repositoryObservable = getModel().loadTrendingRepositoriesInWeeklyWithReactor();

        Observable.combineLatest(showCaseObservable, repositoryObservable,
                new BiFunction<List<ShowCase>, List<Repo>, Map<String, Object>>()
                {
                    @Override public Map<String, Object> apply(List<ShowCase> showCases, List<Repo> repos)
                    {
                        Map<String, Object> map = new HashMap<>(2);
                        map.put(STATE_SHOWCASE, showCases);
                        map.put(STATE_REPOSITORIES, repos);
                        return map;
                    }
                }).doOnSubscribe(new Consumer<Disposable>()
        {
            @Override public void accept(Disposable disposable)
            {
                getUIView().showEmptyView();
                CircleProgressDialog.showLoadingDialog(getAndroidContext());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Map<String, Object>>()
                {
                    @Override public void accept(Map<String, Object> result)
                    {
                        CircleProgressDialog.hideLoadingDialog();

                        ArrayList<ShowCase> showCases = (ArrayList<ShowCase>) result.get(STATE_SHOWCASE);
                        ArrayList<Repo> repositories = (ArrayList<Repo>) result.get(STATE_REPOSITORIES);

                        ExploreFragment.this.showCases = showCases;
                        ExploreFragment.this.repositories = repositories;

                        getUIView().hideEmptyView();
                        getUIView().showShowCaseList(showCases);
                        getUIView().showTrendingRepositoryList(repositories);
                    }
                }, new Consumer<Throwable>()
                {
                    @Override public void accept(Throwable throwable)
                    {
                        CircleProgressDialog.hideLoadingDialog();
                        if (getModel().isEnableCached())
                        {
                            List<ShowCase> showCases = getModel().getShowCasesFromCache();
                            if (showCases != null)
                            {
                                getUIView().showShowCaseList(showCases);
                            }
                        }
                        getUIView().showErrorView(
                                ViewProvider.handleError(getMainController().getConfig().isDebug(), throwable));
                    }
                });
    }
}

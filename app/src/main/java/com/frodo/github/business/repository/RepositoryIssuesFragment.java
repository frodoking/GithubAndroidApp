package com.frodo.github.business.repository;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.dto.response.Issue;
import com.frodo.github.view.CircleProgressDialog;
import com.frodo.github.view.ViewProvider;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function4;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by frodo on 16/6/11.
 */
public class RepositoryIssuesFragment extends StatedFragment<RepositoryIssuesView, RepositoryModel>
{

    private boolean isAccount;

    private String repoOwner;

    private String repo;

    private List<Issue> openIssues;

    private List<Issue> closedIssues;

    private List<Issue> yoursIssues;

    @Override public RepositoryIssuesView createUIView(Context context, LayoutInflater inflater, ViewGroup container)
    {
        return new RepositoryIssuesView(this, inflater, container);
    }

    @Override public void onResume()
    {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Issues");
    }

    @Override public void onFirstTimeLaunched()
    {
        Bundle bundle = getArguments();
        //TODO issues_account
        // issues_repo_{owner}_{repo}
        if (bundle != null && bundle.containsKey("issues_args"))
        {
            String[] argsArray = bundle.getString("issues_args").split("_");

            if (argsArray[0].equalsIgnoreCase("issues"))
            {
                if (argsArray[1].equalsIgnoreCase("account"))
                {
                    isAccount = true;
                }
                else if (argsArray[1].equalsIgnoreCase("repo"))
                {
                    repoOwner = argsArray[2];
                    repo = argsArray[3];
                }
                loadOpenIssuesWithReactor();
            }
        }
    }

    @SuppressLint ("CheckResult") public void loadOpenIssuesWithReactor()
    {
        if (checkIssues(openIssues))
            return;

        Observable<List<Issue>> observable;
        if (isAccount)
        {
            observable = getModel().loadIssuesForAccountWithReactor("all", "open", null, null, null, null, -1, -1);
        }
        else
        {
            observable = getModel()
                    .loadIssuesForRepoWithReactor(repoOwner, repo, "all", "open", null, null, null, null, -1, -1);
        }
        observable.doOnNext(new Consumer<List<Issue>>()
        {
            @Override public void accept(List<Issue> issues)
            {
                openIssues = issues;
            }
        });

        handleObservable(observable);
    }

    @SuppressLint ("CheckResult") public void loadClosedIssuesWithReactor()
    {
        if (checkIssues(closedIssues))
            return;

        Observable<List<Issue>> observable;
        if (isAccount)
        {
            observable = getModel().loadIssuesForAccountWithReactor("all", "closed", null, null, null, null, -1, -1);
        }
        else
        {
            observable = getModel()
                    .loadIssuesForRepoWithReactor(repoOwner, repo, "all", "closed", null, null, null, null, -1, -1);
        }
        observable.doOnNext(new Consumer<List<Issue>>()
        {
            @Override public void accept(List<Issue> issues)
            {
                closedIssues = issues;
            }
        });
        handleObservable(observable);
    }

    @SuppressLint ("CheckResult") public void loadYoursIssuesWithReactor()
    {
        if (checkIssues(yoursIssues))
            return;

        Observable<List<Issue>> observable;
        if (isAccount)
        {
            observable = getModel().loadIssuesForAccountWithReactor(null, null, null, null, null, null, -1, -1);
        }
        else
        {
            observable = getModel()
                    .loadIssuesForRepoWithReactor(repoOwner, repo, null, null, null, null, null, null, -1, -1);
        }
        observable.doOnNext(new Consumer<List<Issue>>()
        {
            @Override public void accept(List<Issue> issues)
            {
                yoursIssues = issues;
            }
        });

        handleObservable(observable);
    }

    private boolean checkIssues(List<Issue> issues)
    {
        if (issues != null && !issues.isEmpty())
        {
            getUIView().hideEmptyView();
            getUIView().showDetail(issues);
            return true;
        }
        return false;
    }

    @SuppressLint ("CheckResult") private void handleObservable(Observable<List<Issue>> observable)
    {
        observable.subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>()
        {
            @Override public void accept(Disposable disposable)
            {
                CircleProgressDialog.showLoadingDialog(getAndroidContext());
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Issue>>()
                {
                    @Override public void accept(List<Issue> issues)
                    {
                        CircleProgressDialog.hideLoadingDialog();
                        getUIView().showDetail(issues);
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

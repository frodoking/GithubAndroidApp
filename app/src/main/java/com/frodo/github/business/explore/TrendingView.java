package com.frodo.github.business.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.GithubLanguage;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.business.AbstractUIView;
import com.frodo.github.business.repository.RepositoriesForListViewAdapter;
import com.frodo.github.business.repository.RepositoryFragment;
import com.frodo.github.business.user.DevelopersForListViewAdapter;
import com.frodo.github.business.user.ProfileFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frodo on 16/6/10.
 */
public class TrendingView extends AbstractUIView {
    private Spinner languageSpinner;
    private ListView listView;
    private DevelopersForListViewAdapter developersForListViewAdapter;
    private RepositoriesForListViewAdapter repositoriesForListViewAdapter;

    public TrendingView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.fragment_trending);
    }

    @Override
    public void initView() {
        languageSpinner = (Spinner) getRootView().findViewById(R.id.language_spinner);
        listView = (ListView) getRootView().findViewById(R.id.lv);

        developersForListViewAdapter = new DevelopersForListViewAdapter(getPresenter().getAndroidContext());
        repositoriesForListViewAdapter = new RepositoriesForListViewAdapter(getPresenter().getAndroidContext());
    }

    @Override
    public void registerListener() {
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GithubLanguage language = (GithubLanguage) parent.getItemAtPosition(position);
                ((TrendingFragment)getPresenter()).loadListBy(language.slug);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object item = parent.getItemAtPosition(position);
                if (item instanceof Repo) {
                    Bundle arguments = new Bundle();
                    arguments.putString("repo", ((Repo) item).owner.login + "/" + ((Repo) item).name);
                    FragmentScheduler.nextFragmentWithUniqueTag((FragmentContainerActivity) getPresenter().getAndroidContext(), RepositoryFragment.class, arguments);

                } else if (item instanceof User) {
                    Bundle arguments = new Bundle();
                    arguments.putString("username", ((User) item).login);
                    FragmentScheduler.nextFragmentWithUniqueTag((FragmentContainerActivity) getPresenter().getAndroidContext(), ProfileFragment.class, arguments);

                }
            }
        });
    }

    public void loadLanguages(List<GithubLanguage> languages) {
        List<GithubLanguage> githubLanguages = new ArrayList<>(languages.size() + 1);
        githubLanguages.add(new GithubLanguage("All Languages", ""));
        githubLanguages.addAll(languages);
        languageSpinner.setAdapter(new ArrayAdapter<>(getPresenter().getAndroidContext(), android.R.layout.simple_spinner_item, githubLanguages));
    }

    public void showRepoList(List<Repo> repos) {
        listView.setAdapter(repositoriesForListViewAdapter);
        repositoriesForListViewAdapter.refreshObjects(repos);
    }

    public void showDeveloperList(List<User> developers) {
        listView.setAdapter(developersForListViewAdapter);
        developersForListViewAdapter.refreshObjects(developers);
    }


    @Override
    public void onShowOrHide(boolean isShown) {
        getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
    }
}

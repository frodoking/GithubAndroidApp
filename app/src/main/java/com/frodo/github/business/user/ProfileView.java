package com.frodo.github.business.user;

import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.business.AbstractUIView;
import com.frodo.github.business.repository.RepositoriesForListViewAdapter;
import com.frodo.github.business.repository.RepositoryFragment;
import com.frodo.github.business.repository.RepositoryListFragment;
import com.frodo.github.view.CardViewGroup;
import com.frodo.github.view.FrescoAndIconicsImageView;
import com.frodo.github.view.OcticonView;

import java.util.List;

/**
 * Created by frodo on 2016/5/7.
 */
public class ProfileView extends AbstractUIView {

    private FrescoAndIconicsImageView headSDV;
    private TextView fullnameTV;
    private TextView usernameTV;
    private OcticonView organizationOV;
    private OcticonView locationOV;
    private OcticonView emailOV;
    private OcticonView blogOV;
    private OcticonView sinceOV;

    private Button followBtn;

    private TextView followersTV;
    private TextView starredTV;
    private TextView followingTV;

    private CardViewGroup popularRepositoriesCVG;
    private CardViewGroup contributedToRepositoriesCVG;
    private ListView popularRepositoriesLV;
    private RepositoriesForListViewAdapter popularRepositoryAdapter;
    private ListView contributedToRepositoriesLV;
    private RepositoriesForListViewAdapter contributedToRepositoryAdapter;

    public ProfileView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.fragment_profile);
    }

    @Override
    public void initView() {
        headSDV = (FrescoAndIconicsImageView) getRootView().findViewById(R.id.head_sdv);
        fullnameTV = (TextView) getRootView().findViewById(R.id.fullname_tv);
        usernameTV = (TextView) getRootView().findViewById(R.id.username_tv);
        organizationOV = (OcticonView) getRootView().findViewById(R.id.organization_ov);
        locationOV = (OcticonView) getRootView().findViewById(R.id.location_ov);
        emailOV = (OcticonView) getRootView().findViewById(R.id.email_ov);
        blogOV = (OcticonView) getRootView().findViewById(R.id.blog_ov);
        sinceOV = (OcticonView) getRootView().findViewById(R.id.since_ov);

        followBtn = (Button) getRootView().findViewById(R.id.follow_btn);

        followersTV = (TextView) getRootView().findViewById(R.id.followers_tv);
        starredTV = (TextView) getRootView().findViewById(R.id.starred_tv);
        followingTV = (TextView) getRootView().findViewById(R.id.following_tv);

        popularRepositoriesCVG = (CardViewGroup) getRootView().findViewById(R.id.popular_repositories_cvg);
        contributedToRepositoriesCVG = (CardViewGroup) getRootView().findViewById(R.id.contributed_to_repositories_cvg);

        popularRepositoriesLV = (ListView) popularRepositoriesCVG.getContentView();
        popularRepositoriesCVG.setContentView(popularRepositoriesLV);

        LinearLayout headerView = (LinearLayout) popularRepositoriesCVG.getHeaderView();
        LinearLayout footerView = (LinearLayout) popularRepositoriesCVG.getFooterView();
        headerView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        ((TextView) headerView.findViewById(R.id.text_tv)).setText("Popular repositories");
        ((TextView) footerView.findViewById(R.id.text_tv)).setText("View more repositories");

        popularRepositoryAdapter = new RepositoriesForListViewAdapter(getPresenter().getAndroidContext());
        popularRepositoriesLV.setAdapter(popularRepositoryAdapter);

        contributedToRepositoriesLV = (ListView) contributedToRepositoriesCVG.getContentView();
        contributedToRepositoriesCVG.setContentView(contributedToRepositoriesLV);

        LinearLayout headerView2 = (LinearLayout) contributedToRepositoriesCVG.getHeaderView();
        headerView2.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        ((TextView) headerView2.findViewById(R.id.text_tv)).setText("Repositories contributed to");

        contributedToRepositoryAdapter = new RepositoriesForListViewAdapter(getPresenter().getAndroidContext());
        contributedToRepositoriesLV.setAdapter(contributedToRepositoryAdapter);
    }

    @Override
    public void registerListener() {
        final AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle arguments = new Bundle();
                Repo repo = (Repo) parent.getAdapter().getItem(position);
                if (repo != null) {
                    arguments.putString("repo", repo.owner.login + "/" + repo.name);
                    FragmentScheduler.nextFragmentWithUniqueTag((FragmentContainerActivity) getPresenter().getAndroidContext(), RepositoryFragment.class, arguments);
                }
            }
        };

        popularRepositoriesLV.setOnItemClickListener(onItemClickListener);
        contributedToRepositoriesLV.setOnItemClickListener(onItemClickListener);
    }

    @Override
    public void onShowOrHide(boolean isShown) {
        getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
    }

    public void showDetail(final User user, boolean isLoginUser) {
        headSDV.setImageURI(Uri.parse(user.avatar_url));
        fullnameTV.setText(user.login);
        usernameTV.setText(user.name);
        organizationOV.setText(user.company);
        locationOV.setText(user.location);
        emailOV.setText(user.email);
        blogOV.setText(user.blog);
        sinceOV.setText(user.created_at.toLocaleString());
        followersTV.setText(String.valueOf(user.followers));
        starredTV.setText(String.valueOf(user.starred));
        followingTV.setText(String.valueOf(user.following));

        followBtn.setVisibility(isLoginUser ? View.GONE : View.VISIBLE);

        showRepositoryList(popularRepositoriesLV, popularRepositoryAdapter, user.popularRepositories);
        showRepositoryList(contributedToRepositoriesLV, contributedToRepositoryAdapter, user.contributeToRepositories);

        followersTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString("users_args", String.format("users_user_followers_%s", user.login));
                FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), UserListFragment.class, arguments);
            }
        });
        followingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString("users_args", String.format("users_user_following_%s", user.login));
                FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), UserListFragment.class, arguments);
            }
        });

        popularRepositoriesCVG.getFooterView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString("repos_args", String.format("repos_user_%s", user.login));
                FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), RepositoryListFragment.class, arguments);
            }
        });
    }

    public void showRepositoryList(ListView listView, RepositoriesForListViewAdapter adapter, List<Repo> repositories) {
        if (repositories != null && !repositories.isEmpty()) {
            adapter.refreshObjects(repositories);
            adapter.notifyDataSetChanged();
            ((View) listView.getParent().getParent()).setVisibility(View.VISIBLE);
        } else {
            ((View) listView.getParent().getParent()).setVisibility(View.INVISIBLE);
        }
    }
}

package com.frodo.github.business.account;

import android.content.Context;
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

import com.facebook.drawee.view.SimpleDraweeView;
import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.UIView;
import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.business.repository.RepositoryFragment;
import com.frodo.github.view.BaseListViewAdapter;
import com.frodo.github.view.CardViewGroup;
import com.frodo.github.view.MaxHeightListView;

import java.util.List;

/**
 * Created by frodo on 2016/5/7.
 */
public class ProfileView extends UIView {

    private SimpleDraweeView headSDV;
    private TextView fullnameTV;
    private TextView usernameTV;
    private TextView companyTV;
    private TextView locationTV;
    private TextView emailTV;
    private TextView blogTV;
    private TextView sinceTV;

    private Button followBtn;

    private TextView followersTV;
    private TextView starredTV;
    private TextView followingTV;

    private CardViewGroup popularRepositoriesCVG;
    private CardViewGroup contributedToRepositoriesCVG;
    private ListView popularRepositoriesLV;
    private Adapter popularRepositoryAdapter;
    private ListView contributedToRepositoriesLV;
    private Adapter contributedToRepositoryAdapter;

    public ProfileView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.fragment_profile);
    }

    @Override
    public void initView() {
        headSDV = (SimpleDraweeView) getRootView().findViewById(R.id.head_sdv);
        fullnameTV = (TextView) getRootView().findViewById(R.id.fullname_tv);
        usernameTV = (TextView) getRootView().findViewById(R.id.username_tv);
        companyTV = (TextView) getRootView().findViewById(R.id.company_tv);
        locationTV = (TextView) getRootView().findViewById(R.id.location_tv);
        emailTV = (TextView) getRootView().findViewById(R.id.email_tv);
        blogTV = (TextView) getRootView().findViewById(R.id.blog_tv);
        sinceTV = (TextView) getRootView().findViewById(R.id.since_tv);

        followBtn = (Button) getRootView().findViewById(R.id.follow_btn);

        followersTV = (TextView) getRootView().findViewById(R.id.followers_tv);
        starredTV = (TextView) getRootView().findViewById(R.id.starred_tv);
        followingTV = (TextView) getRootView().findViewById(R.id.following_tv);

        popularRepositoriesCVG = (CardViewGroup) getRootView().findViewById(R.id.popular_repositories_cvg);
        contributedToRepositoriesCVG = (CardViewGroup) getRootView().findViewById(R.id.contributed_to_repositories_cvg);

        popularRepositoriesLV = new MaxHeightListView(getPresenter().getAndroidContext());
        popularRepositoriesCVG.setContentView(popularRepositoriesLV);

        LinearLayout headerView = (LinearLayout) popularRepositoriesCVG.getHeaderView();
        LinearLayout footerView = (LinearLayout) popularRepositoriesCVG.getFooterView();
        headerView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        ((TextView) headerView.findViewById(R.id.text_tv)).setText("Popular repositories");
        ((TextView) footerView.findViewById(R.id.text_tv)).setText("View more repositories");

        popularRepositoryAdapter = new Adapter(getPresenter().getAndroidContext());
        popularRepositoriesLV.setAdapter(popularRepositoryAdapter);


        contributedToRepositoriesLV = new MaxHeightListView(getPresenter().getAndroidContext());
        contributedToRepositoriesCVG.setContentView(contributedToRepositoriesLV);

        LinearLayout headerView2 = (LinearLayout) contributedToRepositoriesCVG.getHeaderView();
        headerView2.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        ((TextView) headerView2.findViewById(R.id.text_tv)).setText("Repositories contributed to");

        contributedToRepositoryAdapter = new Adapter(getPresenter().getAndroidContext());
        contributedToRepositoriesLV.setAdapter(contributedToRepositoryAdapter);
    }

    @Override
    public void registerListener() {
        popularRepositoriesLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle arguments = new Bundle();
                Repo repository = popularRepositoryAdapter.getItem(position);
                if (repository != null) {
                    arguments.putString("repo", repository.name);
                    FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), RepositoryFragment.class, arguments);
                }
            }
        });
    }

    @Override
    public void onShowOrHide(boolean isShown) {
        getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
    }

    public void showDetail(User user) {
        headSDV.setImageURI(Uri.parse(user.avatar_url));
        fullnameTV.setText(user.login);
        usernameTV.setText(user.name);
        companyTV.setText(user.company);
        locationTV.setText(user.location);
        emailTV.setText(user.email);
        blogTV.setText(user.blog);
        sinceTV.setText(user.created_at.toLocaleString());
        followersTV.setText(String.valueOf(user.followers));
        starredTV.setText(String.valueOf(user.starred));
        followingTV.setText(String.valueOf(user.following));

        showRepositoryList(popularRepositoriesLV, popularRepositoryAdapter, user.popularRepositories);
        showRepositoryList(contributedToRepositoriesLV, contributedToRepositoryAdapter, user.contributeToRepositories);
    }

    public void showRepositoryList(ListView listView, Adapter adapter, List<Repo> repositories) {
        if (repositories != null && !repositories.isEmpty()) {
            adapter.refreshObjects(repositories);
            adapter.notifyDataSetChanged();
            ((View) listView.getParent().getParent()).setVisibility(View.VISIBLE);
        } else {
            ((View) listView.getParent().getParent()).setVisibility(View.INVISIBLE);
        }
    }

    private class Adapter extends BaseListViewAdapter<Repo> {
        public Adapter(Context context) {
            super(context, R.layout.view_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder vh;
            if (convertView == null) {
                convertView = inflateItemView();
                vh = new ViewHolder();
                vh.repoTV = (TextView) convertView.findViewById(R.id.title_tv);
                vh.starCountTV = (TextView) convertView.findViewById(R.id.subtitle_tv);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            final Repo bean = getItem(position);

            vh.repoTV.setText(bean.name);
            vh.starCountTV.setText(String.valueOf(bean.stargazers_count));
            return convertView;
        }

        class ViewHolder {
            TextView repoTV;
            TextView starCountTV;
        }
    }
}

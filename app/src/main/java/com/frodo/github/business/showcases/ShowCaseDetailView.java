package com.frodo.github.business.showcases;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.UIView;
import com.frodo.app.android.core.toolbox.FragmentScheduler;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.R;
import com.frodo.github.bean.Repository;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.business.repository.RepositoryFragment;
import com.frodo.github.view.VerticalSpaceItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by frodo on 2016/5/3.
 */
public class ShowCaseDetailView extends UIView {

    private TextView descriptionTV;
    private RecyclerView repositoriesRV;
    private Adapter repositoryAdapter;
    private List<Repository> repositoryList = new ArrayList<>();

    public ShowCaseDetailView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.fragment_showcase_detail);
    }

    @Override
    public void initView() {
        descriptionTV = (TextView) getRootView().findViewById(R.id.showcase_detail_description_tv);
        repositoriesRV = (RecyclerView) getRootView().findViewById(R.id.showcase_detail_repositories_rv);
        repositoriesRV.addItemDecoration(new VerticalSpaceItemDecoration(ResourceManager.getDimensionPixelSize(R.dimen.margin_middle)));
        repositoriesRV.setLayoutManager(new LinearLayoutManager(getPresenter().getAndroidContext()));
        repositoryAdapter = new Adapter(getRootView().getContext(), repositoryList);
        repositoriesRV.setAdapter(repositoryAdapter);
    }

    @Override
    public void registerListener() {
    }

    @Override
    public void onShowOrHide(boolean isShown) {
        getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
    }

    public void showShowCaseDetail(ShowCase showCase) {
        descriptionTV.setText(showCase.description);
        if (showCase.repositories != null && showCase.repositories.length > 0) {
            repositoryList.clear();
            repositoryList.addAll(Arrays.asList(showCase.repositories));
            repositoryAdapter.notifyDataSetChanged();
        }
    }

    class Adapter extends RecyclerView.Adapter<Adapter.RepositoriesViewHolder> {
        private LayoutInflater layoutInflater;
        private List<Repository> repositories;

        public Adapter(Context context, List<Repository> repositories) {
            this.repositories = repositories;
            this.layoutInflater = LayoutInflater.from(context);
        }


        @Override
        public RepositoriesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RepositoriesViewHolder(layoutInflater.inflate(R.layout.view_showcase_repositories_item, parent, false));
        }

        @Override
        public void onBindViewHolder(RepositoriesViewHolder holder, int position) {
            final Repository bean = repositories.get(position);
            if (bean.owner != null && bean.owner.avatarUrl != null) {
                holder.ownerHeadIV.setImageURI(Uri.parse(bean.owner.avatarUrl));
            }

            holder.repoTV.setText(bean.full_name);
            holder.languageTV.setText(TextUtils.isEmpty(bean.language) ? "" : bean.language);
            holder.starCountTV.setText(String.format("%s stars", bean.stargazers_count));
            holder.descriptionTV.setText(bean.description);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle arguments = new Bundle();
                    arguments.putString("repo", bean.name);
                    FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), RepositoryFragment.class, arguments);
                }
            });
        }

        @Override
        public int getItemCount() {
            return repositories.size();
        }

        class RepositoriesViewHolder extends RecyclerView.ViewHolder {
            SimpleDraweeView ownerHeadIV;
            TextView repoTV;
            TextView languageTV;
            TextView starCountTV;
            TextView descriptionTV;

            public RepositoriesViewHolder(View itemView) {
                super(itemView);
                ownerHeadIV = (SimpleDraweeView) itemView.findViewById(R.id.owner_head_iv);
                repoTV = (TextView) itemView.findViewById(R.id.repo_tv);
                languageTV = (TextView) itemView.findViewById(R.id.language_tv);
                starCountTV = (TextView) itemView.findViewById(R.id.star_count_tv);
                descriptionTV = (TextView) itemView.findViewById(R.id.description_tv);
            }
        }
    }
}

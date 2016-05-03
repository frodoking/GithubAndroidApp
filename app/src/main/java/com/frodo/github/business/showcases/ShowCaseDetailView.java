package com.frodo.github.business.showcases;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.UIView;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.R;
import com.frodo.github.bean.Repository;
import com.frodo.github.bean.ShowCase;
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

    public void showError(String message) {
        if (isOnShown()) {
            Toast.makeText(getPresenter().getAndroidContext(), message, Toast.LENGTH_SHORT).show();
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
                Glide.with(getPresenter().getAndroidContext())
                        .load(bean.owner.avatarUrl)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(holder.ownerHeadIV);
            }

            holder.repoTV.setText(bean.full_name);
            holder.languageTV.setText(TextUtils.isEmpty(bean.language) ? "" : bean.language);
            holder.starCountTV.setText(String.format("%s stars", bean.stargazers_count));
            holder.descriptionTV.setText(bean.description);
        }

        @Override
        public int getItemCount() {
            return repositories.size();
        }

        class RepositoriesViewHolder extends RecyclerView.ViewHolder {
            ImageView ownerHeadIV;
            TextView repoTV;
            TextView languageTV;
            TextView starCountTV;
            TextView descriptionTV;

            public RepositoriesViewHolder(View itemView) {
                super(itemView);
                ownerHeadIV = (ImageView) itemView.findViewById(R.id.owner_head_iv);
                repoTV = (TextView) itemView.findViewById(R.id.repo_tv);
                languageTV = (TextView) itemView.findViewById(R.id.language_tv);
                starCountTV = (TextView) itemView.findViewById(R.id.star_count_tv);
                descriptionTV = (TextView) itemView.findViewById(R.id.description_tv);
            }
        }
    }
}

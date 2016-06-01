package com.frodo.github.business.repository;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.view.BaseRecyclerViewAdapter;

/**
 * Created by frodo on 2016/6/1.
 */
public class RepositoriesAdapter extends BaseRecyclerViewAdapter<Repo, RepositoriesAdapter.ViewHolder> {

    public RepositoriesAdapter(Context context) {
        super(context, R.layout.view_repositories_detail_item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (getItemViewType(viewType) == HEADER) {
            View view = inflateItemView(parent);
            view.setVisibility(View.INVISIBLE);
            return new ViewHolder(view);
        }
        return new ViewHolder(inflateItemView(parent));
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return ITEM;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (getItemViewType(position) == HEADER) {
        } else {
            final Repo repo = getItem(position - 1);
            if (repo.owner != null && repo.owner.avatar_url != null) {
                holder.ownerHeadIV.setImageURI(Uri.parse(repo.owner.avatar_url));
            }

            holder.repoTV.setText(repo.full_name);
            holder.starCountTV.setText(String.format("%s stars", repo.stargazers_count));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle arguments = new Bundle();
                    arguments.putString("repo", repo.name);
                    FragmentScheduler.nextFragment((FragmentContainerActivity) getContext(), RepositoryFragment.class, arguments);
                }
            });
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView ownerHeadIV;
        TextView repoTV;
        TextView starCountTV;

        public ViewHolder(View itemView) {
            super(itemView);
            ownerHeadIV = (SimpleDraweeView) itemView.findViewById(R.id.owner_head_iv);
            repoTV = (TextView) itemView.findViewById(R.id.repo_tv);
            starCountTV = (TextView) itemView.findViewById(R.id.star_count_tv);
        }
    }

}

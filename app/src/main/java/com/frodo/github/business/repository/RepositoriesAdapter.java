package com.frodo.github.business.repository;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.view.BaseRecyclerViewAdapter;
import com.frodo.github.view.OcticonView;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by frodo on 2016/6/1.
 */
public class RepositoriesAdapter extends BaseRecyclerViewAdapter<Repo, RepositoriesAdapter.ViewHolder> {

    public RepositoriesAdapter(Context context) {
        super(context, R.layout.view_item);
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
                holder.repoOV.getFrescoAndIconicsImageView().setImageURI(Uri.parse(repo.owner.avatar_url));
            }

            holder.repoOV.setText(repo.owner.login + "/" + repo.name);
            holder.starCountOV.setText(String.format("%s stars", repo.stargazers_count));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle arguments = new Bundle();
                    arguments.putString("repo", repo.owner.login + "/" + repo.name);
                    FragmentScheduler.nextFragmentWithUniqueTag((FragmentContainerActivity) getContext(), RepositoryFragment.class, arguments);
                }
            });
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        OcticonView repoOV;
        OcticonView starCountOV;

        public ViewHolder(View itemView) {
            super(itemView);
            repoOV = (OcticonView) itemView.findViewById(R.id.title_ov);
            starCountOV = (OcticonView) itemView.findViewById(R.id.subtitle_ov);

            repoOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_repo);
            starCountOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_star);
        }
    }

}

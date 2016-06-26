package com.frodo.github.business.repository;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.toolbox.DrawableProvider;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.GithubComment;
import com.frodo.github.bean.dto.response.Issue;
import com.frodo.github.bean.dto.response.IssueState;
import com.frodo.github.bean.dto.response.Label;
import com.frodo.github.business.AbstractUIView;
import com.frodo.github.business.user.ProfileFragment;
import com.frodo.github.view.BaseRecyclerViewAdapter;
import com.frodo.github.view.FlowLayout;
import com.frodo.github.view.FrescoAndIconicsImageView;
import com.frodo.github.view.OcticonView;
import com.frodo.github.view.VerticalSpaceItemDecoration;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

import us.feras.mdv.MarkdownView;

/**
 * Created by frodo on 2016/6/24.
 */

public class CommentsView extends AbstractUIView {

    private RecyclerView recyclerView;
    private Adapter adapter;

    public CommentsView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.view_recyclerview);
    }

    @Override
    public void initView() {
        recyclerView = (RecyclerView) getRootView().findViewById(R.id.rv);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(ResourceManager.getDimensionPixelSize(R.dimen.margin_middle)));
        recyclerView.setLayoutManager(new LinearLayoutManager(getPresenter().getAndroidContext()));
        adapter = new Adapter(getPresenter().getAndroidContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void registerListener() {
    }

    public void showComments(Issue issue, List<GithubComment> comments) {
        if (issue != null) {
            adapter.setIssue(issue);
        }

        if (comments != null && !comments.isEmpty()) {
            adapter.refreshObjects(comments);
        }
    }

    @Override
    public void onShowOrHide(boolean isShown) {
        getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
    }

    static class Adapter extends BaseRecyclerViewAdapter<GithubComment, CommentsView.Adapter.CommentsViewHolder> {
        private Issue issue;

        public Adapter(Context context) {
            super(context, R.layout.view_comments_item);
        }

        @Override
        public CommentsView.Adapter.CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CommentsView.Adapter.CommentsViewHolder(viewType == 1 ?
                    LayoutInflater.from(mContext).inflate(R.layout.view_comments_head, parent, false) :
                    inflateItemView(parent), viewType);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 1;
            }
            return super.getItemViewType(position);
        }

        @Override
        public int getItemCount() {
            return super.getItemCount() + 1;
        }

        public void setIssue(Issue issue) {
            this.issue = issue;
        }

        @Override
        public void onBindViewHolder(CommentsView.Adapter.CommentsViewHolder holder, int position) {
            if (position == 0) {
                if (issue != null) {
                    int labelRadius = ResourceManager.getDimensionPixelSize(R.dimen.corner_radius_default);
                    DrawableProvider.fillViewBackgroundDrawable(holder.stateOV, DrawableProvider.createGradientDrawable(ResourceManager.getColor(android.R.color.holo_green_dark), labelRadius));
                    if (issue.state.equals(IssueState.closed)) {
                        holder.stateOV.setText("Closed");
                        holder.userTV.setText(issue.user.login + " closed this issue");
                        holder.stateOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_issue_closed);
                    } else {
                        holder.stateOV.setText("Open");
                        holder.userTV.setText(issue.user.login + " opened this issue");
                        holder.stateOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_issue_opened);
                    }
                    holder.titleTV.setText(issue.title);
                    holder.headFIIV.setImageURI(Uri.parse(issue.user.avatar_url));
                    holder.dateTV.setText(issue.created_at.toLocaleString());

                    ViewGroup.MarginLayoutParams labelLp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    int margin = ResourceManager.getDimensionPixelSize(R.dimen.margin_small);
                    labelLp.setMargins(margin, margin, margin, margin);

                    if (issue.labels != null && !issue.labels.isEmpty()) {
                        ((View) holder.labelsFL.getParent()).setVisibility(View.VISIBLE);
                        for (Label label : issue.labels) {
                            TextView labelTV = new TextView(getContext());
                            labelTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                            labelTV.setPadding(5, 0, 5, 0);
                            labelTV.setText(label.name);

                            try {
                                DrawableProvider.fillViewBackgroundDrawable(labelTV, DrawableProvider.createGradientDrawable(Color.parseColor("#" + label.color), labelRadius));
                                labelTV.setTextColor(ResourceManager.getColor(android.R.color.white));
                            } catch (Exception e) {
                                DrawableProvider.fillViewBackgroundDrawable(labelTV, DrawableProvider.createGradientDrawable(ResourceManager.getColor(android.R.color.darker_gray), labelRadius));
                                labelTV.setTextColor(ResourceManager.getColor(android.R.color.black));
                            }

                            holder.labelsFL.addView(labelTV, labelLp);
                        }
                    } else {
                        ((View) holder.labelsFL.getParent()).setVisibility(View.GONE);
                    }

                    holder.bodyMDVMaster.loadMarkdown(issue.body == null ? "No description given" : issue.body);
                }
                return;
            }

            final GithubComment comment = getItem(position - 1);

            holder.userOV.getFrescoAndIconicsImageView().setImageURI(Uri.parse(comment.user.avatar_url));
            holder.userOV.setText(comment.user.login);
            holder.dateOV.setText(comment.created_at.toLocaleString());
            holder.bodyMDV.loadMarkdown(comment.body);

            ((View) holder.userOV.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle arguments = new Bundle();
                    arguments.putString("username", comment.user.login);
                    FragmentScheduler.nextFragment((FragmentContainerActivity) getContext(), ProfileFragment.class, arguments);
                }
            });
        }

        class CommentsViewHolder extends RecyclerView.ViewHolder {
            int viewType;

            // HEAD
            OcticonView stateOV;
            TextView titleTV;
            FrescoAndIconicsImageView headFIIV;
            TextView userTV;
            TextView dateTV;
            FlowLayout labelsFL;
            MarkdownView bodyMDVMaster;

            // ITEM
            OcticonView userOV;
            OcticonView dateOV;
            MarkdownView bodyMDV;

            CommentsViewHolder(View itemView, int viewType) {
                super(itemView);
                this.viewType = viewType;

                if (viewType == 1) {
                    stateOV = (OcticonView) itemView.findViewById(R.id.state_ov);
                    titleTV = (TextView) itemView.findViewById(R.id.title_tv);
                    headFIIV = (FrescoAndIconicsImageView) itemView.findViewById(R.id.head_fiiv);
                    userTV = (TextView) itemView.findViewById(R.id.user_tv);
                    dateTV = (TextView) itemView.findViewById(R.id.date_tv);

                    labelsFL = (FlowLayout) itemView.findViewById(R.id.labels_ll);
                    bodyMDVMaster = (MarkdownView) itemView.findViewById(R.id.body_mdv);
                } else {
                    userOV = (OcticonView) itemView.findViewById(R.id.title_ov);
                    userOV.resizeIcon(ResourceManager.getDimensionPixelSize(R.dimen.image_size_default));
                    dateOV = (OcticonView) itemView.findViewById(R.id.subtitle_ov);
                    dateOV.noIcon();
                    userOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_mark_github);
                    bodyMDV = (MarkdownView) itemView.findViewById(R.id.body_mdv);
                }
            }
        }
    }
}

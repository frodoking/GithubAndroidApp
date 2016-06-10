package com.frodo.github.business.activity;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.GithubEvent;
import com.frodo.github.bean.dto.response.events.EventType;
import com.frodo.github.business.AbstractUIView;
import com.frodo.github.view.BaseRecyclerViewAdapter;
import com.frodo.github.view.OcticonView;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by frodo on 16/6/10.
 */
public class EventsView extends AbstractUIView {
    private RecyclerView recyclerView;
    private BaseRecyclerViewAdapter adapter;

    public EventsView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.view_recyclerview);
    }

    @Override
    public void initView() {
        recyclerView = (RecyclerView) getRootView().findViewById(R.id.rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getPresenter().getAndroidContext()));

        adapter = new EventsAdapter(getPresenter().getAndroidContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void registerListener() {

    }

    public void showDetail(List<GithubEvent> events) {
        adapter.refreshObjects(events);
    }

    private static class EventsAdapter extends BaseRecyclerViewAdapter<GithubEvent, EventsAdapter.ViewHolder> {

        public EventsAdapter(Context context) {
            super(context, R.layout.view_events_item);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(inflateItemView(parent));
        }

        @Override
        public void onBindViewHolder(ViewHolder vh, int position) {
            final GithubEvent event = getItem(position);
            if (event.type.equals(EventType.IssueCommentEvent)) {
                vh.titleOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_comment_discussion);
                vh.titleOV.setText(String.format("%s commented on issue %s#%s", event.actor.login, event.repo.name, event.payload.issue.number));
                vh.actorOV.setVisibility(View.VISIBLE);

                vh.actorOV.getFrescoAndIconicsImageView().setImageURI(Uri.parse(event.payload.comment.user.avatar_url));
                vh.actorOV.setText(event.payload.comment.body);
            } else if (event.type.equals(EventType.WatchEvent)) {
                vh.titleOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_star);
                vh.titleOV.setText(String.format("%s starred %s", event.actor.login, event.repo.name));
                vh.actorOV.setVisibility(View.GONE);
            } else if (event.type.equals(EventType.CreateEvent)) {
                vh.titleOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_repo);
                vh.titleOV.setText(String.format("%s created repository %s", event.actor.login, event.repo.name));
                vh.actorOV.setVisibility(View.GONE);
            } else if (event.type.equals(EventType.ForkEvent)) {
                vh.titleOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_git_branch);
                vh.titleOV.setText(String.format("%s forked %s", event.actor.login, event.repo.name));
                vh.actorOV.setVisibility(View.GONE);
            } else {
                vh.titleOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_unmute);
                vh.titleOV.setText("unknown");
                vh.actorOV.setVisibility(View.GONE);
            }
            vh.dateTV.setText(DateFormat.getDateInstance().format(event.created_at));
        }


        class ViewHolder extends RecyclerView.ViewHolder {
            OcticonView titleOV;
            OcticonView actorOV;
            TextView dateTV;

            public ViewHolder(View itemView) {
                super(itemView);
                titleOV = (OcticonView) itemView.findViewById(R.id.title_ov);
                actorOV = (OcticonView) itemView.findViewById(R.id.actor_ov);
                actorOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_mark_github);
                dateTV = (TextView) itemView.findViewById(R.id.date_tv);
            }
        }
    }


}

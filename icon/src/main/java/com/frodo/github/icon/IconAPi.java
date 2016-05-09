package com.frodo.github.icon;

import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frodo on 2016/4/28.
 */
public class IconApi {

    private static final Integer[] iconIds = {
            R.drawable.octicon_book,
            R.drawable.octicon_code,
            R.drawable.octicon_eye,
            R.drawable.octicon_git_branch,
            R.drawable.octicon_git_pull_request,
            R.drawable.octicon_graph,
            R.drawable.octicon_history,
            R.drawable.octicon_issue_opened,
            R.drawable.octicon_organization,
            R.drawable.octicon_pulse,
            R.drawable.octicon_repo,
            R.drawable.octicon_repo_forked,
            R.drawable.octicon_star,
            R.drawable.octicon_tag,
            R.drawable.octicon_file_directory,
            R.drawable.octicon_file_text,
            R.drawable.octicon_megaphone,
            R.drawable.octicon_hubot,
            R.drawable.octicon_flame,
            R.drawable.octicon_location,
            R.drawable.octicon_email,
            R.drawable.octicon_link,
            R.drawable.octicon_clock,
    };

    private static final String[] iconTexts = {
            "book",
            "code",
            "eye",
            "git_branch",
            "git_pull_request",
            "graph",
            "history",
            "issue_opened",
            "organization",
            "pulse",
            "repo",
            "repo_forked",
            "star",
            "tag",
            "file_directory",
            "file_text",
            "megaphone",
            "hubot",
            "flame",
            "location",
            "email",
            "link",
            "clock",
    };

    public int getLayoutId(){
        return R.layout.icon_api_layout;
    }

    public void initView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        GridLayoutManager layoutManager = new GridLayoutManager(view.getContext(), 6);
        recyclerView.setLayoutManager(layoutManager);
        List<Pair<Integer, String>> list = new ArrayList<>(iconIds.length);
        for (int i = 0; i < iconIds.length; i++) {
            Pair<Integer, String> pair = new Pair<>(iconIds[i], iconTexts[i]);
            list.add(pair);
        }
        recyclerView.setAdapter(new MyAdapter(list));
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MViewHolder> {

        private List<Pair<Integer, String>> list;

        public MyAdapter(List<Pair<Integer, String>> list) {
            this.list = list;
        }

        @Override
        public MViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.icon_item, null);
            return new MViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MViewHolder holder, int position) {
            holder.mTextView.setText(list.get(position).second);
            holder.mImageView.setImageResource(list.get(position).first);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class MViewHolder extends RecyclerView.ViewHolder {

            public TextView mTextView;
            public ImageView mImageView;

            public MViewHolder(View view) {
                super(view);
                this.mTextView = (TextView) view.findViewById(R.id.text_tv);
                this.mImageView = (ImageView) itemView.findViewById(R.id.img_iv);
            }
        }
    }
}

package com.frodo.github.business.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.UIView;
import com.frodo.github.R;
import com.frodo.github.bean.Repository;
import com.frodo.github.bean.User;
import com.frodo.github.view.BaseListViewAdapter;

/**
 * Created by frodo on 2016/5/7.
 */
public class ProfileView extends UIView {

    private ListView popularRepositoriesLV;
    private Adapter popularRepositoryAdapter;
    private ListView popularRepositoriesLV;
    private Adapter popularRepositoryAdapter;

    public ProfileView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.fragment_profile);
    }

    @Override
    public void initView() {

    }

    @Override
    public void registerListener() {

    }

    public void showDetail(User user) {

    }

    private class Adapter extends BaseListViewAdapter<Repository> {
        public Adapter(Context context) {
            super(context, R.layout.view_repositories_item2);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder vh;
            if (convertView == null) {
                convertView = inflateItemView();
                vh = new ViewHolder();
                vh.repoTV = (TextView) convertView.findViewById(R.id.repo_tv);
                vh.starCountTV = (TextView) convertView.findViewById(R.id.star_count_tv);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            final Repository bean = getItem(position);

            vh.repoTV.setText(bean.full_name);
            vh.starCountTV.setText(String.valueOf(bean.stargazers_count));
            return convertView;
        }

        class ViewHolder {
            TextView repoTV;
            TextView starCountTV;
        }
    }
}

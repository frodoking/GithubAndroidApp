package com.frodo.github.business.repository;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Issue;
import com.frodo.github.view.BaseListViewAdapter;
import com.frodo.github.view.FrescoAndIconicsImageView;

import java.text.DateFormat;

/**
 * Created by frodo on 2016/6/4.
 */
public class IssuesForListViewAdapter extends BaseListViewAdapter<Issue> {
    public IssuesForListViewAdapter(Context context) {
        super(context, R.layout.view_issues_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = inflateItemView();
            vh = new ViewHolder();
            vh.stateFIIV = (FrescoAndIconicsImageView) convertView.findViewById(R.id.state_fiiv);
            vh.titleTV = (TextView) convertView.findViewById(R.id.title_tv);
            vh.stateInfoTV = (TextView) convertView.findViewById(R.id.state_info_tv);
            vh.labelsLL = (LinearLayout) convertView.findViewById(R.id.labels_ll);
            vh.numberTV = (TextView) convertView.findViewById(R.id.number_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final Issue issue = getItem(position);
        vh.titleTV.setText(issue.title);
        vh.stateInfoTV.setText(String.format("%s by %s at %s", issue.state, issue.user.login, DateFormat.getDateTimeInstance().format(issue.updated_at)));
        vh.numberTV.setText(String.format("#%s", issue.number));
        return convertView;
    }

    class ViewHolder {
        FrescoAndIconicsImageView stateFIIV;
        TextView titleTV;
        TextView stateInfoTV;
        LinearLayout labelsLL;
        TextView numberTV;
    }
}

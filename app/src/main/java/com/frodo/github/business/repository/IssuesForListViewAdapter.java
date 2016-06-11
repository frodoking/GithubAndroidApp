package com.frodo.github.business.repository;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Issue;
import com.frodo.github.bean.dto.response.IssueState;
import com.frodo.github.bean.dto.response.Label;
import com.frodo.github.view.AutoTabLayout;
import com.frodo.github.view.BaseListViewAdapter;
import com.frodo.github.view.FrescoAndIconicsImageView;
import com.mikepenz.octicons_typeface_library.Octicons;

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
            vh.labelsATL = (AutoTabLayout) convertView.findViewById(R.id.labels_ll);
            vh.numberTV = (TextView) convertView.findViewById(R.id.number_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final Issue issue = getItem(position);
        if (issue.state.equals(IssueState.open)){
            vh.stateFIIV.setColor(ResourceManager.getColor(android.R.color.holo_green_light));
            vh.stateFIIV.setIcon(Octicons.Icon.oct_issue_opened);
        } else if (issue.state.equals(IssueState.closed)){
            vh.stateFIIV.setColor(ResourceManager.getColor(android.R.color.holo_red_light));
            vh.stateFIIV.setIcon(Octicons.Icon.oct_issue_closed);
        }

        vh.titleTV.setText(issue.title);
        vh.stateInfoTV.setText(String.format("%s by %s at %s", issue.state, issue.user.login, DateFormat.getDateTimeInstance().format(issue.updated_at)));
        vh.numberTV.setText(String.format("#%s", issue.number));

        if (issue.labels != null && !issue.labels.isEmpty()) {
            for (Label label : issue.labels) {
                TextView labelTV = new TextView(getContext());
                labelTV.setText(label.name);

                try {
                    labelTV.setBackgroundColor(Color.parseColor("#" + label.color));
                    labelTV.setTextColor(ResourceManager.getColor(android.R.color.white));
                } catch (Exception e) {
                    labelTV.setBackgroundColor(ResourceManager.getColor(android.R.color.darker_gray));
                    labelTV.setTextColor(ResourceManager.getColor(android.R.color.black));
                }

                vh.labelsATL.addView(labelTV);
            }
        }
        return convertView;
    }

    class ViewHolder {
        FrescoAndIconicsImageView stateFIIV;
        TextView titleTV;
        TextView stateInfoTV;
        AutoTabLayout labelsATL;
        TextView numberTV;
    }
}

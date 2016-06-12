package com.frodo.github.business.repository;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frodo.app.android.core.toolbox.DrawableProvider;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Issue;
import com.frodo.github.bean.dto.response.IssueState;
import com.frodo.github.bean.dto.response.Label;
import com.frodo.github.view.BaseListViewAdapter;
import com.frodo.github.view.FlowLayout;
import com.frodo.github.view.FrescoAndIconicsImageView;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.text.DateFormat;

/**
 * Created by frodo on 2016/6/4.
 */
public class IssuesForListViewAdapter extends BaseListViewAdapter<Issue> {
    private ViewGroup.MarginLayoutParams labelLp;
    private int labelRadius;

    public IssuesForListViewAdapter(Context context) {
        super(context, R.layout.view_issues_item);
        labelLp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = ResourceManager.getDimensionPixelSize(R.dimen.margin_small);
        labelLp.setMargins(margin, margin, margin, margin);

        labelRadius = ResourceManager.getDimensionPixelSize(R.dimen.corner_radius_default);
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
            vh.labelsATL = (FlowLayout) convertView.findViewById(R.id.labels_ll);
            vh.numberTV = (TextView) convertView.findViewById(R.id.number_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final Issue issue = getItem(position);
        if (issue.state.equals(IssueState.open)) {
            vh.stateFIIV.setColor(ResourceManager.getColor(android.R.color.holo_green_light));
            vh.stateFIIV.setIcon(Octicons.Icon.oct_issue_opened);
        } else if (issue.state.equals(IssueState.closed)) {
            vh.stateFIIV.setColor(ResourceManager.getColor(android.R.color.holo_red_light));
            vh.stateFIIV.setIcon(Octicons.Icon.oct_issue_closed);
        }

        vh.titleTV.setText(issue.title);
        vh.stateInfoTV.setText(String.format("%s by %s at %s", issue.state, issue.user.login, DateFormat.getDateTimeInstance().format(issue.updated_at)));
        vh.numberTV.setText(String.format("#%s", issue.number));

        vh.labelsATL.removeAllViews();
        if (issue.labels != null && !issue.labels.isEmpty()) {
            for (Label label : issue.labels) {
                TextView labelTV = new TextView(getContext());
                labelTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                labelTV.setPadding(5,0,5,0);
                labelTV.setText(label.name);

                try {
                    DrawableProvider.fillViewBackgroundDrawable(labelTV, DrawableProvider.createGradientDrawable(Color.parseColor("#" + label.color), labelRadius));
                    labelTV.setTextColor(ResourceManager.getColor(android.R.color.white));
                } catch (Exception e) {
                    DrawableProvider.fillViewBackgroundDrawable(labelTV, DrawableProvider.createGradientDrawable(ResourceManager.getColor(android.R.color.darker_gray), labelRadius));
                    labelTV.setTextColor(ResourceManager.getColor(android.R.color.black));
                }

                vh.labelsATL.addView(labelTV, labelLp);
            }
        }
        return convertView;
    }

    class ViewHolder {
        FrescoAndIconicsImageView stateFIIV;
        TextView titleTV;
        TextView stateInfoTV;
        FlowLayout labelsATL;
        TextView numberTV;
    }
}

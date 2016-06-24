package com.frodo.github.business.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Notification;
import com.frodo.github.business.AbstractUIView;
import com.frodo.github.view.BaseListViewAdapter;
import com.frodo.github.view.OcticonView;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.List;

/**
 * Created by frodo on 16/6/10.
 */
public class NotificationsView extends AbstractUIView {

    private View readSignV;
    private ListView lv;
    private NotificationsAdapter adapter;

    public NotificationsView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.uiview_notifications);
    }

    @Override
    public void initView() {
        readSignV = getRootView().findViewById(R.id.read_sign_v);
        OcticonView titleOV = (OcticonView) readSignV.findViewById(R.id.title_ov);
        titleOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_bell);
        titleOV.setText("Unread notifications (5)");

        OcticonView moreOV = (OcticonView) readSignV.findViewById(R.id.subtitle_ov);
        moreOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_chevron_down);

        lv = (ListView) getRootView().findViewById(R.id.lv);
        adapter = new NotificationsAdapter(getPresenter().getAndroidContext());
        lv.setAdapter(adapter);
    }

    @Override
    public void registerListener() {
    }

    public void showDetail(List<Notification> notifications) {
        adapter.refreshObjects(notifications);
    }

    private static class NotificationsAdapter extends BaseListViewAdapter<Notification> {

        public NotificationsAdapter(Context context) {
            super(context, R.layout.view_notifications_item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder vh;
            if (convertView == null) {
                convertView = inflateItemView();
                vh = new ViewHolder(convertView);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            final Notification notification = getItem(position);
            vh.titleOV.setText(notification.subject.title);
            vh.dateTV.setText(String.format("Updated %s by %s", notification.updated_at, notification.repository.owner.login));

            return convertView;
        }

        class ViewHolder {
            OcticonView titleOV;
            TextView dateTV;

            ViewHolder(View view) {
                titleOV = (OcticonView) view.findViewById(R.id.title_ov);
                titleOV.getFrescoAndIconicsImageView().setColor(ResourceManager.getColor(android.R.color.holo_green_light));
                titleOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_issue_opened);
                dateTV = (TextView) view.findViewById(R.id.date_tv);
            }
        }
    }
}

package com.frodo.github.business;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.view.BaseListViewAdapter;

/**
 * Created by frodo on 2016/5/31.
 */
public class DevelopersAdapter extends BaseListViewAdapter<User> {
    public DevelopersAdapter(Context context) {
        super(context, R.layout.view_developers_item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder vh;
        if (convertView == null) {
            convertView = inflateItemView();
            vh = new ViewHolder();
            vh.ownerHeadIV = (SimpleDraweeView) convertView.findViewById(R.id.head_sdv);
            vh.nameTV = (TextView) convertView.findViewById(R.id.name_tv);
            vh.infoTV = (TextView) convertView.findViewById(R.id.info_tv);
            vh.ownerTV = (TextView) convertView.findViewById(R.id.owner_tv);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        final User user = getItem(position);
        if (user.avatar_url != null) {
            vh.ownerHeadIV.setImageURI(Uri.parse(user.avatar_url));
        }

        vh.nameTV.setText(user.login + "(" + user.name + ")");
        vh.infoTV.setText(user.company);
        vh.infoTV.setCompoundDrawables(ResourceManager.getDrawable(R.drawable.octicon_organization), null, null, null);
        vh.ownerTV.setText(String.valueOf(user.public_repos));
        return convertView;
    }

    class ViewHolder {
        SimpleDraweeView ownerHeadIV;
        TextView nameTV;
        TextView infoTV;
        TextView ownerTV;
    }
}

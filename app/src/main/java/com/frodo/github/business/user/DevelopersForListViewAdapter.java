package com.frodo.github.business.user;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.view.BaseListViewAdapter;
import com.frodo.github.view.OcticonView;

/**
 * Created by frodo on 16/6/19.
 */
public class DevelopersForListViewAdapter extends BaseListViewAdapter<User> {
	public DevelopersForListViewAdapter(Context context) {
		super(context, R.layout.view_developers_item);
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

		final User user = getItem(position);
		if (user.avatar_url != null) {
			vh.ownerHeadIV.setImageURI(Uri.parse(user.avatar_url));
		}

		if (TextUtils.isEmpty(user.name)) {
			vh.nameTV.setText(user.login);
		} else {
			vh.nameTV.setText(String.format("%s(%s)", user.login, user.name));
		}

		vh.infoTV.setText(TextUtils.isEmpty(user.company) ? "" : user.company);
		vh.ownerOV.setText(user.public_repos == 0 ? "" : String.valueOf(user.public_repos));
		return convertView;
	}

	class ViewHolder {
		SimpleDraweeView ownerHeadIV;
		TextView nameTV;
		TextView infoTV;
		OcticonView ownerOV;

		public ViewHolder(View itemView) {
			ownerHeadIV = (SimpleDraweeView) itemView.findViewById(R.id.head_sdv);
			nameTV = (TextView) itemView.findViewById(R.id.name_tv);
			infoTV = (TextView) itemView.findViewById(R.id.info_tv);
			ownerOV = (OcticonView) itemView.findViewById(R.id.owner_ov);
		}
	}
}

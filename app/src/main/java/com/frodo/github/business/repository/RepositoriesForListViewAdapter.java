package com.frodo.github.business.repository;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;

import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.view.BaseListViewAdapter;
import com.frodo.github.view.OcticonView;
import com.mikepenz.octicons_typeface_library.Octicons;

/**
 * Created by frodo on 2016/6/4.
 */
public class RepositoriesForListViewAdapter extends BaseListViewAdapter<Repo> {

	private boolean isNeedLoadOwnerAvatar = true;

	public RepositoriesForListViewAdapter(Context context) {
		this(context, true);
	}

	public RepositoriesForListViewAdapter(Context context, boolean isNeedLoadOwnerAvatar) {
		super(context, R.layout.view_item);
		this.isNeedLoadOwnerAvatar = isNeedLoadOwnerAvatar;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder vh;
		if (convertView == null) {
			convertView = inflateItemView();
			vh = new ViewHolder();
			vh.repoOV = (OcticonView) convertView.findViewById(R.id.title_ov);
			vh.starCountOV = (OcticonView) convertView.findViewById(R.id.subtitle_ov);
			vh.repoOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_repo);
			vh.starCountOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_star);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		final Repo repo = getItem(position);
		if (isNeedLoadOwnerAvatar) {
			if (repo.owner != null && repo.owner.avatar_url != null) {
				vh.repoOV.getFrescoAndIconicsImageView().setImageURI(Uri.parse(repo.owner.avatar_url));
			}
		}

		if (repo.owner != null) {
			vh.repoOV.setText(repo.owner.login + "/" + repo.name);
		} else {
			vh.repoOV.setText(repo.name);
		}

		vh.starCountOV.setText(String.valueOf(repo.stargazers_count));
		return convertView;
	}

	class ViewHolder {
		OcticonView repoOV;
		OcticonView starCountOV;
	}
}

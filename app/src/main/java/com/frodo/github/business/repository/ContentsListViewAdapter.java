package com.frodo.github.business.repository;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Content;
import com.frodo.github.bean.dto.response.ContentType;
import com.frodo.github.view.BaseListViewAdapter;
import com.frodo.github.view.OcticonView;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.util.Collections;
import java.util.List;

/**
 * Created by frodo on 2016/6/7.
 */
public class ContentsListViewAdapter extends BaseListViewAdapter<Content> {
	public ContentsListViewAdapter(Context context) {
		super(context, R.layout.view_git_tree_item);
	}

	@Override
	public void refreshObjects(List<Content> list) {
		Collections.sort(list);
		super.refreshObjects(list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder vh;
		if (convertView == null) {
			convertView = inflateItemView();
			vh = new ViewHolder();
			vh.titleOV = (OcticonView) convertView.findViewById(R.id.title_ov);
			vh.updateTimeTV = (TextView) convertView.findViewById(R.id.update_time_tv);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		final Content content = getItem(position);
		if (content.type.equals(ContentType.dir)) {
			vh.titleOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_file_directory);
		} else if (content.type.equals(ContentType.file)) {
			if (content.name.endsWith(".md")) {
				vh.titleOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_book);
			} else {
				vh.titleOV.getFrescoAndIconicsImageView().setIcon(Octicons.Icon.oct_file_text);
			}
		}
		vh.titleOV.setText(content.name);
		vh.updateTimeTV.setText(String.valueOf(content.size));

		return convertView;
	}

	class ViewHolder {
		OcticonView titleOV;
		TextView updateTimeTV;
	}
}

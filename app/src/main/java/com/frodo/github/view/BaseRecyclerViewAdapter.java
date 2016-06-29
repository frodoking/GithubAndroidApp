package com.frodo.github.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frodo on 2016/6/1.
 */
public abstract class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

	protected static final int HEADER = 0;
	protected static final int ITEM = 1;

	protected Context mContext;
	protected int resource;
	private LayoutInflater inflater;
	private List<T> items = new ArrayList<>();

	public BaseRecyclerViewAdapter(Context context, int resource) {
		this(context, resource, new ArrayList<T>());
	}

	public BaseRecyclerViewAdapter(Context context, int resource, List<T> objects) {
		this.mContext = context;
		this.resource = resource;
		addObjects(objects);
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	public Context getContext() {
		return this.mContext;
	}

	public T getItem(int position) {
		return items.get(position);
	}

	public View inflateItemView(ViewGroup parent) {
		if (inflater == null) {
			inflater = LayoutInflater.from(mContext);
		}
		return inflater.inflate(resource, parent, false);
	}

	public void addObjects(List<T> list) {
		if (list != null) {
			items.addAll(list);
			notifyDataSetChanged();
		}
	}

	public void refreshObjects(List<T> list) {
		this.removeObjects();
		this.addObjects(list);
		notifyDataSetChanged();
	}

	public void removeObjects() {
		items.clear();
	}
}

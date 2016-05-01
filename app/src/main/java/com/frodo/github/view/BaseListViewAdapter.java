package com.frodo.github.view;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frodo on 2016/5/1.
 */
public abstract class BaseListViewAdapter<T> extends ArrayAdapter<T> {

    protected Context mContext;
    protected int resource;
    private LayoutInflater inflater;
    private List<T> array = new ArrayList<>();

    public BaseListViewAdapter(Context context, int resource) {
        this(context, resource, new ArrayList<T>());
    }

    public BaseListViewAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.resource = resource;
        array.addAll(objects);
    }

    public List<T> getArray() {
        return array;
    }

    public View inflateItemView() {
        if (inflater == null) {
            inflater = LayoutInflater.from(mContext);
        }
        return inflater.inflate(resource, null);
    }

    public void refreshObjects(List<T> list) {
        this.removeObjects();
        this.addObjects(list);
        array.clear();
        array.addAll(list);
    }

    public void addObjects(List<T> list) {
        if (list != null) {
            array.addAll(list);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                super.addAll(list);
            } else {
                for (T t : list) {
                    super.add(t);
                }
            }
        }
    }

    public void removeObjects() {
        this.clear();
        array.clear();
    }
}


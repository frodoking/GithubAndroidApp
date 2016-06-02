package com.frodo.github;

import android.content.Context;
import android.view.View;

import com.frodo.github.datasource.JsoupApi;
import com.frodo.github.icon.IconicsTestFragment;
import com.frodo.github.icon.StaticOcticonsFragment;

/**
 * Created by frodo on 2016/6/2.
 */
public abstract class StaticFragment {
    protected Context context;

    public static StaticFragment get(Context context, String tag) {
        if (tag.equalsIgnoreCase("IconicsTest")) {
            return new IconicsTestFragment(context);
        } else if (tag.equalsIgnoreCase("StaticOcticons")) {
            return new StaticOcticonsFragment(context);
        }
        if (tag.equalsIgnoreCase("JsoupApi")) {
            return new JsoupApi(context);
        }
        return null;
    }

    public StaticFragment(Context context) {
        this.context = context;
    }

    public abstract int getLayoutId();

    public abstract void initView(View view);

    public abstract String tag();
}

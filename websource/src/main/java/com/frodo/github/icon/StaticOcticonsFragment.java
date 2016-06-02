package com.frodo.github.icon;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.frodo.github.StaticFragment;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.typeface.ITypeface;

import java.util.AbstractList;
import java.util.ArrayList;

/**
 * Created by frodo on 2016/6/1.
 */
public class StaticOcticonsFragment extends StaticFragment {
    private ArrayList<String> icons = new ArrayList<>();
    private IconAdapter mAdapter;
    private boolean randomize;
    private String search;

    public StaticOcticonsFragment(Context context) {
        super(context);
    }

    public void randomize(boolean randomize) {
        this.randomize = randomize;
        if (this.mAdapter != null) {
            this.mAdapter.setRandomized(this.randomize);
            this.mAdapter.notifyDataSetChanged();
        }
    }

    public int getLayoutId() {
        return R.layout.icons_fragment;
    }

    public void initView(View view) {
        // Init and Setup RecyclerView
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        //animator not yet working
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new IconAdapter(randomize, new ArrayList<String>(), R.layout.row_icon);
        recyclerView.setAdapter(mAdapter);

        for (ITypeface iTypeface : Iconics.getRegisteredFonts(context)) {
            if (iTypeface.getFontName().equalsIgnoreCase("Octicons")) {
                if (iTypeface.getIcons() != null) {
                    for (String icon : iTypeface.getIcons()) {
                        icons.add(icon);
                    }
                    mAdapter.setIcons(randomize, icons);
                    break;
                }
            }
        }
        //filter if a search param was provided
        onSearch(search);
    }

    @Override
    public String tag() {
        return "StaticOcticons";
    }


    public void onSearch(String s) {
        search = s;

        if (mAdapter != null) {
            if (TextUtils.isEmpty(s)) {
                mAdapter.clear();
                mAdapter.setIcons(randomize, icons);
                mAdapter.notifyDataSetChanged();
            } else {
                AbstractList<String> tmpList = new ArrayList<>();
                for (String icon : icons) {
                    if (icon.toLowerCase().contains(s.toLowerCase())) {
                        tmpList.add(icon);
                    }
                }
                mAdapter.clear();
                mAdapter.setIcons(randomize, tmpList);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

}

package com.frodo.github.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.github.R;

/**
 * Created by frodo on 2016/5/14.
 */
public class CardViewGroup extends CardView {

    private LinearLayout rootViewGroup;
    private View headerLayout;
    private View contentLayout;
    private View footerLayout;

    public CardViewGroup(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public CardViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public CardViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }


    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        setRadius(ResourceManager.getDimensionPixelSize(R.dimen.corner_radius_default));
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardViewGroup);
        rootViewGroup = new LinearLayout(context);
        rootViewGroup.setOrientation(LinearLayout.VERTICAL);
        addView(rootViewGroup);
        if (a.hasValue(R.styleable.CardViewGroup_headerLayout)) {
            headerLayout = View.inflate(context, a.getResourceId(R.styleable.CardViewGroup_headerLayout, 0), rootViewGroup);
            rootViewGroup.addView(ViewProvider.getLine(context, rootViewGroup.getOrientation()));
        }
        if (a.hasValue(R.styleable.CardViewGroup_contentLayout)) {
            contentLayout = View.inflate(context, a.getResourceId(R.styleable.CardViewGroup_contentLayout, 0), rootViewGroup);
        }
        if (a.hasValue(R.styleable.CardViewGroup_footerLayout)) {
            rootViewGroup.addView(ViewProvider.getLine(context, rootViewGroup.getOrientation()));
            footerLayout = View.inflate(context, a.getResourceId(R.styleable.CardViewGroup_footerLayout, 0), rootViewGroup);
        }

        a.recycle();
    }

    public void setHeaderLayout(View headerView) {
        this.headerLayout = headerView;
        rootViewGroup.removeAllViews();
        reFillView();
    }

    public View getHeaderView() {
        return headerLayout;
    }

    public void setContentView(View contentView) {
        this.contentLayout = contentView;
        rootViewGroup.removeAllViews();
        reFillView();
    }

    public View getContentView() {
        return contentLayout;
    }

    public void setFooterView(View footerView) {
        this.footerLayout = footerView;
        rootViewGroup.removeAllViews();
        reFillView();
    }

    public View getFooterView() {
        return footerLayout;
    }

    private void reFillView() {
        if (headerLayout!=null){
            rootViewGroup.addView(headerLayout);
            rootViewGroup.addView(ViewProvider.getLine(getContext(), rootViewGroup.getOrientation()));
        }

        rootViewGroup.addView(contentLayout);

        if (footerLayout!=null){
            rootViewGroup.addView(ViewProvider.getLine(getContext(), rootViewGroup.getOrientation()));
            rootViewGroup.addView(footerLayout);
        }
    }
}

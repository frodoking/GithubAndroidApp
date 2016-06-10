package com.frodo.github.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.github.R;

/**
 * Created by frodo on 2016/5/14.
 */
public class CardViewGroup extends CardView {

    private LayoutInflater inflater;
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
        if (isInEditMode()) {
            return;
        }

        inflater = LayoutInflater.from(context);

        setRadius(ResourceManager.getDimensionPixelSize(R.dimen.corner_radius_default));
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardViewGroup);
        rootViewGroup = new LinearLayout(context);
        rootViewGroup.setOrientation(LinearLayout.VERTICAL);
        addView(rootViewGroup);
        if (a.hasValue(R.styleable.CardViewGroup_headerLayout)) {
            headerLayout = inflater.inflate(a.getResourceId(R.styleable.CardViewGroup_headerLayout, 0), null, false);
            rootViewGroup.addView(headerLayout);
            rootViewGroup.addView(ViewProvider.getLine(context, rootViewGroup.getOrientation()));
        }
        if (a.hasValue(R.styleable.CardViewGroup_contentLayout)) {
            contentLayout = inflater.inflate(a.getResourceId(R.styleable.CardViewGroup_contentLayout, 0), null, false);
            rootViewGroup.addView(contentLayout);
        }
        if (a.hasValue(R.styleable.CardViewGroup_footerLayout)) {
            rootViewGroup.addView(ViewProvider.getLine(context, rootViewGroup.getOrientation()));
            footerLayout = inflater.inflate(a.getResourceId(R.styleable.CardViewGroup_footerLayout, 0), null, false);
            rootViewGroup.addView(footerLayout);
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

    public View getContentView() {
        return contentLayout;
    }

    public void setContentView(View contentView) {
        this.contentLayout = contentView;
        rootViewGroup.removeAllViews();
        reFillView();
    }

    public View getFooterView() {
        return footerLayout;
    }

    public void setFooterView(View footerView) {
        this.footerLayout = footerView;
        rootViewGroup.removeAllViews();
        reFillView();
    }

    private void reFillView() {
        if (headerLayout != null) {
            rootViewGroup.addView(headerLayout);
            rootViewGroup.addView(ViewProvider.getLine(getContext(), rootViewGroup.getOrientation()));
        }

        if (contentLayout != null) {
            rootViewGroup.addView(contentLayout);
        }

        if (footerLayout != null) {
            rootViewGroup.addView(ViewProvider.getLine(getContext(), rootViewGroup.getOrientation()));
            rootViewGroup.addView(footerLayout);
        }
    }
}

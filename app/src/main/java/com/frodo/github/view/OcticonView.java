package com.frodo.github.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.github.R;
import com.mikepenz.iconics.utils.Utils;

/**
 * Created by frodoking on 2016/6/1.
 */
public class OcticonView extends LinearLayout {
    private FrescoAndIconicsImageView frescoAndIconicsImageView;
    private TextView textView;
    private String direction = "start";

    public OcticonView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public OcticonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public OcticonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        if (isInEditMode()) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OcticonView);
        frescoAndIconicsImageView = new FrescoAndIconicsImageView(context, attrs, defStyleAttr);
        textView = new TextView(context, attrs, defStyleAttr);

        if (a.hasValue(R.styleable.OcticonView_iconDirection)) {
            String direction = a.getString(R.styleable.OcticonView_iconDirection);
            if (direction != null) {
                if (direction.equalsIgnoreCase("start")) {
                    addIconView(a, frescoAndIconicsImageView);
                    addTextView(a, textView, false);
                } else if (direction.equalsIgnoreCase("end")) {
                    addTextView(a, textView, true);
                    addIconView(a, frescoAndIconicsImageView);
                } else {
                    addIconView(a, frescoAndIconicsImageView);
                    addTextView(a, textView, false);
                }
            } else {
                addIconView(a, frescoAndIconicsImageView);
                addTextView(a, textView, false);
            }
        }
        a.recycle();
    }

    private void addIconView(TypedArray a, View view) {
        if (a.hasValue(R.styleable.OcticonView_iconSize)) {
            int size = a.getDimensionPixelSize(R.styleable.OcticonView_iconSize, 0);
            if (size != 0) {
                addView(view, new LayoutParams(size, LayoutParams.MATCH_PARENT));
            } else {
                addView(view);
            }
        }
    }

    private void addTextView(TypedArray a, TextView textView, boolean isRight) {
        if (a.hasValue(R.styleable.OcticonView_iconPadding)) {
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            if (isRight) {
                params.rightMargin = a.getDimensionPixelSize(R.styleable.OcticonView_iconPadding, 0);
            } else {
                params.leftMargin = a.getDimensionPixelSize(R.styleable.OcticonView_iconPadding, 0);
            }
            addView(textView, params);
        } else {
            addView(textView);
        }
    }

    public FrescoAndIconicsImageView getFrescoAndIconicsImageView() {
        return frescoAndIconicsImageView;
    }

    public void setPaddingDp(int paddingDp) {
        if (frescoAndIconicsImageView != null) {
            int padding = Utils.convertDpToPx(getContext(), paddingDp);
            if (direction.equalsIgnoreCase("start")) {
                ((LayoutParams) frescoAndIconicsImageView.getLayoutParams()).leftMargin = padding;
            } else {
                ((LayoutParams) frescoAndIconicsImageView.getLayoutParams()).rightMargin = padding;
            }
        }
    }

    public TextView getTextView() {
        return textView;
    }

    public final void setText(CharSequence text) {
        textView.setText(text);
    }

    public final void setTextColor(int color){
        textView.setTextColor(color);
    }

    public final void setTextColorRes(int colorRes){
        setTextColor(ResourceManager.getColor(colorRes));
    }
}

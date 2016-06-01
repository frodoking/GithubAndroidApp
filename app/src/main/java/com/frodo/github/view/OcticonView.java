package com.frodo.github.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frodo.github.R;
import com.mikepenz.iconics.view.IconicsImageView;

/**
 * Created by frodoking on 2016/6/1.
 */
public class OcticonView extends LinearLayout {
    private IconicsImageView iconicsImageView;
    private TextView textView;

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
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OcticonView);
        iconicsImageView = new IconicsImageView(context, attrs, defStyleAttr);
        textView = new TextView(context, attrs, defStyleAttr);

        if (a.hasValue(R.styleable.OcticonView_iconDirection)) {
            String direction = a.getString(R.styleable.OcticonView_iconDirection);
            if (direction != null) {
                if (direction.equalsIgnoreCase("start")) {
                    addView(iconicsImageView);
                    addView(textView);
                } else if (direction.equalsIgnoreCase("end")) {
                    addView(textView);
                    addView(iconicsImageView);
                } else {
                    addView(iconicsImageView);
                    addView(textView);
                }
            } else {
                addView(iconicsImageView);
                addView(textView);
            }
        }
        a.recycle();
    }

    public IconicsImageView getIconicsImageView() {
        return iconicsImageView;
    }

    public TextView getTextView() {
        return textView;
    }

    public final void setText(CharSequence text) {
        textView.setText(text);
    }
}

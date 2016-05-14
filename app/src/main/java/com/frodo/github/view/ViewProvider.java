package com.frodo.github.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by frodo on 2016/5/14.
 */
public class ViewProvider {
    public static View getLine(Context context, int orientation) {
        View view = new View(context);
        view.setBackgroundResource(android.R.color.darker_gray);
        if (orientation == LinearLayout.HORIZONTAL) {
            view.setLayoutParams(new LinearLayout.LayoutParams(1, LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        }
        return view;
    }
}

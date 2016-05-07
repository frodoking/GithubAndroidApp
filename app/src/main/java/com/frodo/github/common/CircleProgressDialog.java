package com.frodo.github.common;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.widget.LinearLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.frodo.app.android.core.toolbox.DensityUtils;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.github.R;

/**
 * Created by frodo on 16/5/7.
 */
public class CircleProgressDialog extends Dialog {
    private static CircleProgressDialog dialog;

    public CircleProgressDialog(Context context) {
        super(context);
        init();
    }

    public CircleProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected CircleProgressDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    public static Dialog showLoadingDialog(Context context) {
        if (dialog == null) {
            dialog = new CircleProgressDialog(context, R.style.CustomProgressDialog);
        }

        if (!dialog.isShowing()) {
            dialog.show();
        }


        return dialog;
    }

    public static void hideLoadingDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public static void destoryLoadingDialog() {
        if (dialog != null) {
            dialog = null;
        }
    }

    private void init() {
        LinearLayout ll = new LinearLayout(getContext());
        SimpleDraweeView simpleDraweeView = new SimpleDraweeView(getContext());
        simpleDraweeView.setBackgroundColor(ResourceManager.getColor(R.color.colorAccent));
        int size = DensityUtils.dp2px(getContext(), 50);
        ll.addView(simpleDraweeView, new LinearLayout.LayoutParams(size, size));
        Uri uri = Uri.parse("assets://octocat-spinner-128.gif");
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        simpleDraweeView.setController(draweeController);
        setContentView(ll);

        setCanceledOnTouchOutside(false);
    }
}

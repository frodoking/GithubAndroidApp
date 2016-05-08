package com.frodo.github.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.widget.LinearLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.frodo.app.android.core.toolbox.DensityUtils;
import com.frodo.app.framework.log.Logger;
import com.frodo.github.R;

/**
 * Created by frodo on 16/5/7.
 */
public class CircleProgressDialog extends Dialog {
    private static CircleProgressDialog dialog;

    private SimpleDraweeView draweeView;

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
        draweeView = new SimpleDraweeView(getContext());
        int size = DensityUtils.dp2px(getContext(), 45);
        ll.addView(draweeView, new LinearLayout.LayoutParams(size, size));
        Uri uri = Uri.parse("https://assets-cdn.github.com/images/spinners/octocat-spinner-128.gif");
        ControllerListener<ImageInfo> controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (animatable != null) {
                    animatable.start();
                }
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                Logger.fLog().tag("CircleProgressDialog").e("onFailure", throwable);
            }
        };
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(controllerListener)
                .build();
        draweeView.setController(draweeController);
        setContentView(ll);

        setCanceledOnTouchOutside(false);
    }

    @Override
    public void show() {
        super.show();
        DraweeController draweeController = draweeView.getController();
        if (draweeController != null) {
            Animatable animatable = draweeController.getAnimatable();
            if (animatable != null && !animatable.isRunning()) {
                draweeView.getController().getAnimatable().start();
            }
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        DraweeController draweeController = draweeView.getController();
        if (draweeController != null) {
            Animatable animatable = draweeController.getAnimatable();
            if (animatable != null && animatable.isRunning()) {
                draweeView.getController().getAnimatable().stop();
            }
        }
    }
}

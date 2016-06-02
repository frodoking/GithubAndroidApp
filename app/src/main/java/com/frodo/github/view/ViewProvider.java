package com.frodo.github.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.frodo.app.android.core.toolbox.DrawableHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    public static void saveBitmap(Bitmap bitmap, String path, String fileName) {
        File f = new File(path, fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

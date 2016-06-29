package com.frodo.github.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.frodo.app.framework.exception.HttpException;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.io.File;
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

	public static String handleError(boolean debug, Throwable throwable) {
		if (debug) {
			return Log.getStackTraceString(throwable);
		} else {
			if (throwable instanceof HttpException) {
				return "net error, please tap cat to retry.";
			} else {
				return "have some error, please tap cat to retry";
			}
		}
	}

	public static void wrapNotImplementFeature(final Context context, View v) {
		if (v != null) {
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Toast.makeText(context, "This feature is not implemented, hope you can contribute to this project.", Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			Toast.makeText(context, "This feature is not implemented, hope you can contribute to this project.", Toast.LENGTH_SHORT).show();
		}
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

	public static void updateMenuItem(Context context, Menu menu, int itemId, Octicons.Icon icon) {
		MenuItem menuItem = menu.findItem(itemId);
		if (menuItem != null)
			menuItem.setIcon(new IconicsDrawable(context).icon(icon).sizeDp(16).colorRes(android.R.color.black));
	}
}

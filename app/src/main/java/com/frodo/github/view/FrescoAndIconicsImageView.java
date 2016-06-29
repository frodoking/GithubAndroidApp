package com.frodo.github.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.typeface.IIcon;
import com.mikepenz.iconics.utils.Utils;

/**
 * 使用icon 的时候 代替 placeholder
 * Created by frodo on 2016/6/3.
 */
public class FrescoAndIconicsImageView extends SimpleDraweeView {

	private IconicsDrawable placeholderIcon;
	@ColorInt
	private int mColor = 0;
	private int mSize = -1;
	private int mPadding = -1;
	@ColorInt
	private int mContourColor = 0;
	private int mContourWidth = -1;
	@ColorInt
	private int mBackgroundColor = 0;
	private int mCornerRadius = -1;

	public FrescoAndIconicsImageView(Context context, GenericDraweeHierarchy hierarchy) {
		super(context, hierarchy);
		initialize(context, null, 0);
	}

	public FrescoAndIconicsImageView(Context context) {
		super(context);
		initialize(context, null, 0);
	}

	public FrescoAndIconicsImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context, attrs, 0);
	}

	public FrescoAndIconicsImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initialize(context, attrs, defStyle);
	}

	private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
		if (isInEditMode()) {
			return;
		}

		final TypedArray a = context.obtainStyledAttributes(attrs, com.mikepenz.iconics.core.R.styleable.IconicsImageView, defStyleAttr, 0);
		mColor = a.getColor(com.mikepenz.iconics.core.R.styleable.IconicsImageView_iiv_color, 0);
		mSize = a.getDimensionPixelSize(com.mikepenz.iconics.core.R.styleable.IconicsImageView_iiv_size, -1);
		mPadding = a.getDimensionPixelSize(com.mikepenz.iconics.core.R.styleable.IconicsImageView_iiv_padding, -1);
		mContourColor = a.getColor(com.mikepenz.iconics.core.R.styleable.IconicsImageView_iiv_contour_color, 0);
		mContourWidth = a.getDimensionPixelSize(com.mikepenz.iconics.core.R.styleable.IconicsImageView_iiv_contour_width, -1);
		mBackgroundColor = a.getColor(com.mikepenz.iconics.core.R.styleable.IconicsImageView_iiv_background_color, 0);
		mCornerRadius = a.getDimensionPixelSize(com.mikepenz.iconics.core.R.styleable.IconicsImageView_iiv_corner_radius, -1);

		if (a.hasValue(com.mikepenz.iconics.core.R.styleable.IconicsImageView_iiv_icon)) {
			String icon = a.getString(com.mikepenz.iconics.core.R.styleable.IconicsImageView_iiv_icon);
			setAttributes(new IconicsDrawable(getContext(), icon));
		}

		a.recycle();
	}

	private void setAttributes(IconicsDrawable icon) {
		if (mColor != 0) {
			icon.color(mColor);
		}
		if (mSize != -1) {
			icon.sizePx(mSize);
		}
		if (mPadding != -1) {
			icon.paddingPx(mPadding);
		}
		if (mContourColor != 0) {
			icon.contourColor(mContourColor);
		}
		if (mContourWidth != -1) {
			icon.contourWidthPx(mContourWidth);
		}
		if (mBackgroundColor != 0) {
			icon.backgroundColor(mBackgroundColor);
		}
		if (mCornerRadius != -1) {
			icon.roundedCornersPx(mCornerRadius);
		}

		placeholderIcon = icon;
		setImageDrawable(icon);
		getHierarchy().setPlaceholderImage(icon);
	}

	public void setColor(@ColorInt int color) {
		if (placeholderIcon != null) {
			placeholderIcon.color(color);
		}
		mColor = color;
	}

	public void setPaddingDp(int paddingDp) {
		if (placeholderIcon != null) {
			placeholderIcon.paddingDp(paddingDp);
		}
		mPadding = Utils.convertDpToPx(getContext(), paddingDp);
	}

	public void setBackgroundColor(@ColorInt int color) {
		if (placeholderIcon != null) {
			placeholderIcon.backgroundColor(color);
		}
		mBackgroundColor = color;
	}


	public void setIcon(IIcon icon) {
		setAttributes(new IconicsDrawable(getContext(), icon));
	}
}

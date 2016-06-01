package com.frodo.github.icon;


import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;
import java.util.Random;

/**
 * Created by frodo on 2016/6/1.
 */
public class IconAdapter extends RecyclerView.Adapter<IconAdapter.ViewHolder> {

    private Random random = new Random();
    private boolean randomize;
    private List<String> icons;
    private int rowLayout;


    public IconAdapter(boolean randomize, List<String> icons, int rowLayout) {
        this.randomize = randomize;
        this.icons = icons;
        this.rowLayout = rowLayout;
    }

    public void setIcons(boolean randomize, List<String> icons) {
        this.randomize = randomize;
        this.icons.addAll(icons);
        this.notifyItemRangeInserted(0, icons.size() - 1);
    }

    public void setRandomized(boolean randomize) {
        this.randomize = randomize;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final String icon = icons.get(i);
        viewHolder.image.setIcon(icon);
        viewHolder.name.setText(icon);

        if (randomize) {

            viewHolder.image.setColorRes(getRandomColor(i));
            viewHolder.image.setPaddingDp(random.nextInt(12));

            viewHolder.image.setContourWidthDp(random.nextInt(2));
            viewHolder.image.setContourColor(getRandomColor(i - 2));


            int y = random.nextInt(10);
            if (y % 4 == 0) {
                viewHolder.image.setBackgroundColorRes(getRandomColor(i - 4));
                viewHolder.image.setRoundedCornersDp(2 + random.nextInt(10));
            }
        }
    }

    private int getRandomColor(int i) {
        return R.color.cardview_dark_background;
    }


    @Override
    public int getItemCount() {
        return icons == null ? 0 : icons.size();
    }


    public void clear() {
        icons.clear();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public IconicsImageView image;

        PopupWindow popup;

        public ViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            image = (IconicsImageView) itemView.findViewById(R.id.icon);

            itemView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int a = motionEvent.getAction();
                    if (a == MotionEvent.ACTION_DOWN) {
                        if (popup != null && popup.isShowing()) {
                            popup.dismiss();
                        }
                        ImageView imageView = new ImageView(view.getContext());
                        imageView.setImageDrawable(
                                image.getIcon().clone().sizeDp(144).paddingDp(8).backgroundColor(Color.parseColor("#DDFFFFFF")).roundedCornersDp(12)
                        );
                        int size = 144;
                        popup = new PopupWindow(imageView, size, size);
                        popup.showAsDropDown(itemView);

                        //copy to clipboard
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboard.setText(image.getIcon().getIcon().getFormattedName());
                        } else {
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData.newPlainText("Android-Iconics icon", image.getIcon().getIcon().getFormattedName());
                            clipboard.setPrimaryClip(clip);
                        }
                    } else if (a == MotionEvent.ACTION_UP || a == MotionEvent.ACTION_CANCEL || a == MotionEvent.ACTION_OUTSIDE) {
                        if (popup != null && popup.isShowing()) {
                            popup.dismiss();
                        }
                    }
                    return false;
                }
            });
        }

    }
}

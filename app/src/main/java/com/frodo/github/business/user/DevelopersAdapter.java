package com.frodo.github.business.user;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.view.BaseRecyclerViewAdapter;
import com.frodo.github.view.OcticonView;

import org.w3c.dom.Text;

/**
 * Created by frodo on 2016/5/31.
 */

public class DevelopersAdapter extends BaseRecyclerViewAdapter<User, DevelopersAdapter.ViewHolder> {

    public DevelopersAdapter(Context context) {
        super(context, R.layout.view_developers_item);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (getItemViewType(viewType) == HEADER) {
            View view = inflateItemView(parent);
            view.setVisibility(View.INVISIBLE);
            return new ViewHolder(view);
        }
        return new ViewHolder(inflateItemView(parent));
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER;
        } else {
            return ITEM;
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        if (getItemViewType(position) == HEADER) {
        } else {
            final User user = getItem(position - 1);
            if (user.avatar_url != null) {
                vh.ownerHeadIV.setImageURI(Uri.parse(user.avatar_url));
            }

            if (TextUtils.isEmpty(user.name)) {
                vh.nameTV.setText(user.login);
            } else {
                vh.nameTV.setText(String.format("%s(%s)", user.login, user.name));
            }

            vh.infoTV.setText(TextUtils.isEmpty(user.company) ? "" : user.company);
            vh.ownerOV.setText(user.public_repos == 0 ? "" : String.valueOf(user.public_repos));

            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle arguments = new Bundle();
                    arguments.putString("username", user.login);
                    FragmentScheduler.nextFragmentWithUniqueTag((FragmentContainerActivity) getContext(), ProfileFragment.class, arguments);
                }
            });
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView ownerHeadIV;
        TextView nameTV;
        TextView infoTV;
        OcticonView ownerOV;

        public ViewHolder(View itemView) {
            super(itemView);
            ownerHeadIV = (SimpleDraweeView) itemView.findViewById(R.id.head_sdv);
            nameTV = (TextView) itemView.findViewById(R.id.name_tv);
            infoTV = (TextView) itemView.findViewById(R.id.info_tv);
            ownerOV = (OcticonView) itemView.findViewById(R.id.owner_ov);
        }
    }
}

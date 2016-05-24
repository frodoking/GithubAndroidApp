package com.frodo.github.business.repository;

import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.UIView;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.github.DrawableHelper;
import com.frodo.github.R;
import com.frodo.github.bean.Repository;
import com.frodo.github.view.CardViewGroup;

/**
 * Created by frodo on 2016/5/7.
 */
public class RepositoryView extends UIView {
    private TextView descriptionTV;
    private TextView privateTV;
    private TextView issuesCountTV;
    private TextView dateTV;
    private TextView languageTV;
    private TextView sizeTV;
    private TextView ownerTV;

    private Button starBtn;
    private Button watchBtn;

    private TextView stargazersTV;
    private TextView watchersTV;
    private TextView forksTV;

    private CardViewGroup branchsCVG;

    public RepositoryView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.fragment_repository);
    }

    @Override
    public void initView() {
        descriptionTV = (TextView) getRootView().findViewById(R.id.repo_description_tv);
        privateTV = (TextView) getRootView().findViewById(R.id.private_tv);
        issuesCountTV = (TextView) getRootView().findViewById(R.id.issues_count_tv);
        dateTV = (TextView) getRootView().findViewById(R.id.date_tv);
        languageTV = (TextView) getRootView().findViewById(R.id.language_tv);
        sizeTV = (TextView) getRootView().findViewById(R.id.size_tv);
        ownerTV = (TextView) getRootView().findViewById(R.id.owner_tv);

        starBtn = (Button) getRootView().findViewById(R.id.star_btn);
        watchBtn = (Button) getRootView().findViewById(R.id.watch_btn);

        stargazersTV = (TextView) getRootView().findViewById(R.id.stargazers_tv);
        watchersTV = (TextView) getRootView().findViewById(R.id.watchers_tv);
        forksTV = (TextView) getRootView().findViewById(R.id.forks_tv);

        branchsCVG = (CardViewGroup) getRootView().findViewById(R.id.branchs_cvg);

        TextView viewCodeTV = (TextView) branchsCVG.getFooterView().findViewById(R.id.repo_view_code_tv);
        TextView jumpToFileTV = (TextView) branchsCVG.getFooterView().findViewById(R.id.repo_jump_to_file_tv);
        viewCodeTV.setCompoundDrawablesRelativeWithIntrinsicBounds(
                DrawableHelper.withContext(getPresenter().getAndroidContext())
                .withColor(R.color.colorPrimary)
                .withDrawable(R.drawable.octicon_file_directory).tint().get(), null,null,null);
        jumpToFileTV.setCompoundDrawablesRelativeWithIntrinsicBounds(
                DrawableHelper.withContext(getPresenter().getAndroidContext())
                .withColor(R.color.colorPrimary)
                .withDrawable(R.drawable.octicon_search).tint().get(), null,null,null);
    }

    @Override
    public void registerListener() {
    }

    public void showDetail(Repository repository) {
        descriptionTV.setText(repository.description);
        issuesCountTV.setText(String.format("%s issues",repository.open_issues_count));
        languageTV.setText(repository.language);
        sizeTV.setText(String.format("%s KB", repository.size));
        dateTV.setText(repository.created_at.toLocaleString());
        ownerTV.setText(repository.owner.login);
        privateTV.setText(repository.isPrivate ? "Private" : "Public");

        stargazersTV.setText(String.valueOf(repository.stargazers_count));
        watchersTV.setText(String.valueOf(repository.subscribers_count));
        forksTV.setText(String.valueOf(repository.forks_count));

        TextView branchsTV = (TextView) branchsCVG.getHeaderView().findViewById(R.id.title_tv);
        TextView branchsSubTV = (TextView) branchsCVG.getHeaderView().findViewById(R.id.subtitle_tv);
        branchsTV.setText(repository.default_branch);
        branchsSubTV.setText("");
        branchsTV.setCompoundDrawablesWithIntrinsicBounds(ResourceManager.getDrawable(R.drawable.octicon_git_branch),null,null,null);
        branchsSubTV.setCompoundDrawablesWithIntrinsicBounds(ResourceManager.getDrawable(R.drawable.octicon_chevron_down),null,null,null);

        LinearLayout ll = (LinearLayout) branchsCVG.getContentView();
        TextView textView = new TextView(getPresenter().getAndroidContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setText("Latest commit by frodoking 7 days ago");
        textView.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ResourceManager.getDimensionPixelSize(R.dimen.item_height_default));
        int margin  = ResourceManager.getDimensionPixelSize(R.dimen.margin_middle);
        lp.leftMargin = margin;
        lp.rightMargin = margin;
        ll.addView(textView, lp);
    }
}

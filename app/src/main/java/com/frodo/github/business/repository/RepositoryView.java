package com.frodo.github.business.repository;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.UIView;
import com.frodo.app.android.core.toolbox.DrawableHelper;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.view.CardViewGroup;
import com.frodo.github.view.OcticonView;

/**
 * Created by frodo on 2016/5/7.
 */
public class RepositoryView extends UIView {
    private TextView descriptionTV;
    private OcticonView privateOV;
    private OcticonView issuesCountOV;
    private OcticonView dateOV;
    private OcticonView languageOV;
    private OcticonView sizeOV;
    private OcticonView ownerOV;

    private Button starBtn;
    private Button watchBtn;

    private TextView stargazersTV;
    private TextView watchersTV;
    private TextView forksTV;

    private CardViewGroup branchsCVG;
    private TextView branchsTV;
    private TextView branchCommitTV;

    private CardViewGroup readmeCVG;
    private CardViewGroup pulseCVG;
    private CardViewGroup issuesCVG;
    private CardViewGroup pullRequestsCVG;
    private CardViewGroup notificationsCVG;

    public RepositoryView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.fragment_repository);
    }

    @Override
    public void initView() {
        descriptionTV = (TextView) getRootView().findViewById(R.id.repo_description_tv);
        privateOV = (OcticonView) getRootView().findViewById(R.id.private_ov);
        issuesCountOV = (OcticonView) getRootView().findViewById(R.id.issues_count_ov);
        dateOV = (OcticonView) getRootView().findViewById(R.id.date_ov);
        languageOV = (OcticonView) getRootView().findViewById(R.id.language_ov);
        sizeOV = (OcticonView) getRootView().findViewById(R.id.size_ov);
        ownerOV = (OcticonView) getRootView().findViewById(R.id.owner_ov);

        starBtn = (Button) getRootView().findViewById(R.id.star_btn);
        watchBtn = (Button) getRootView().findViewById(R.id.watch_btn);

        stargazersTV = (TextView) getRootView().findViewById(R.id.stargazers_tv);
        watchersTV = (TextView) getRootView().findViewById(R.id.watchers_tv);
        forksTV = (TextView) getRootView().findViewById(R.id.forks_tv);

        initBranchsCardView();
        initReadmeCardView();
        initPulseCardView();
        initIssuesCardView();
        initPullRequestsCardView();
        initNotificationsCardView();
    }

    private void initBranchsCardView() {
        branchsCVG = (CardViewGroup) getRootView().findViewById(R.id.branchs_cvg);

        branchsTV = (TextView) branchsCVG.getHeaderView().findViewById(R.id.title_tv);
        TextView branchsSubTV = (TextView) branchsCVG.getHeaderView().findViewById(R.id.subtitle_tv);
        branchsSubTV.setText("");
        branchsTV.setCompoundDrawablesWithIntrinsicBounds(ResourceManager.getDrawable(R.drawable.octicon_git_branch), null, null, null);
        branchsSubTV.setCompoundDrawablesWithIntrinsicBounds(ResourceManager.getDrawable(R.drawable.octicon_chevron_down), null, null, null);

        LinearLayout ll = (LinearLayout) branchsCVG.getContentView();
        branchCommitTV = new TextView(getPresenter().getAndroidContext());
        branchCommitTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        branchCommitTV.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ResourceManager.getDimensionPixelSize(R.dimen.item_height_default));
        int margin = ResourceManager.getDimensionPixelSize(R.dimen.margin_middle);
        lp.leftMargin = margin;
        lp.rightMargin = margin;
        ll.addView(branchCommitTV, lp);

        branchsCVG = (CardViewGroup) getRootView().findViewById(R.id.branchs_cvg);
        TextView viewCodeTV = (TextView) branchsCVG.getFooterView().findViewById(R.id.repo_view_code_tv);
        TextView jumpToFileTV = (TextView) branchsCVG.getFooterView().findViewById(R.id.repo_jump_to_file_tv);
        viewCodeTV.setCompoundDrawablesRelativeWithIntrinsicBounds(
                DrawableHelper.withContext(getPresenter().getAndroidContext())
                        .withColor(R.color.colorPrimary)
                        .withDrawable(R.drawable.octicon_file_directory).tint().get(), null, null, null);
        jumpToFileTV.setCompoundDrawablesRelativeWithIntrinsicBounds(
                DrawableHelper.withContext(getPresenter().getAndroidContext())
                        .withColor(R.color.colorPrimary)
                        .withDrawable(R.drawable.octicon_search).tint().get(), null, null, null);
    }

    private void initReadmeCardView() {
        readmeCVG = (CardViewGroup) getRootView().findViewById(R.id.readme_cvg);

        TextView titleTV = (TextView) readmeCVG.getHeaderView().findViewById(R.id.title_tv);
        titleTV.setCompoundDrawablesWithIntrinsicBounds(ResourceManager.getDrawable(R.drawable.octicon_book), null, null, null);
        titleTV.setText("README.md");

        TextView footTV = (TextView) readmeCVG.getFooterView().findViewById(R.id.text_tv);
        footTV.setText("View all of README.md");
    }

    private void initPulseCardView() {
        pulseCVG = (CardViewGroup) getRootView().findViewById(R.id.pulse_cvg);

        TextView titleTV = (TextView) pulseCVG.getHeaderView().findViewById(R.id.title_tv);
        TextView subtitleTVTV = (TextView) pulseCVG.getHeaderView().findViewById(R.id.subtitle_tv);
        titleTV.setCompoundDrawablesWithIntrinsicBounds(ResourceManager.getDrawable(R.drawable.octicon_pulse), null, null, null);
        titleTV.setText("Pulse");
        subtitleTVTV.setText("Past week");

        TextView footTV = (TextView) pulseCVG.getFooterView().findViewById(R.id.text_tv);
        footTV.setText("View Pulse");
    }

    private void initIssuesCardView() {
        issuesCVG = (CardViewGroup) getRootView().findViewById(R.id.issues_cvg);

        TextView titleTV = (TextView) issuesCVG.getHeaderView().findViewById(R.id.title_tv);
        titleTV.setCompoundDrawablesWithIntrinsicBounds(ResourceManager.getDrawable(R.drawable.octicon_issue_opened), null, null, null);
        titleTV.setText("Issues");

        TextView footTV = (TextView) issuesCVG.getFooterView().findViewById(R.id.text_tv);
        footTV.setText("View all issues");
    }

    private void initPullRequestsCardView() {
        pullRequestsCVG = (CardViewGroup) getRootView().findViewById(R.id.pull_requests_cvg);

        TextView titleTV = (TextView) pullRequestsCVG.getHeaderView().findViewById(R.id.title_tv);
        titleTV.setCompoundDrawablesWithIntrinsicBounds(ResourceManager.getDrawable(R.drawable.octicon_git_pull_request), null, null, null);
        titleTV.setText("Pull Requests");

        TextView footTV = (TextView) pullRequestsCVG.getFooterView().findViewById(R.id.text_tv);
        footTV.setText("View all pull requests");
    }

    private void initNotificationsCardView() {
        notificationsCVG = (CardViewGroup) getRootView().findViewById(R.id.notifications_cvg);

        TextView titleTV = (TextView) notificationsCVG.getHeaderView().findViewById(R.id.title_tv);
        titleTV.setCompoundDrawablesWithIntrinsicBounds(ResourceManager.getDrawable(R.drawable.octicon_bell), null, null, null);
        titleTV.setText("Notifications");
    }

    @Override
    public void registerListener() {
    }

    public void showDetail(Repo repository) {
        descriptionTV.setText(repository.description);
        issuesCountOV.setText(String.format("%s issues", repository.open_issues_count));
        languageOV.setText(repository.language);
        sizeOV.setText(String.format("%s KB", repository.size));
        dateOV.setText(repository.created_at.toLocaleString());
        ownerOV.setText(repository.owner.login);
        privateOV.setText(repository.isPrivate ? "Private" : "Public");

        stargazersTV.setText(String.valueOf(repository.stargazers_count));
        watchersTV.setText(String.valueOf(repository.subscribers_count));
        forksTV.setText(String.valueOf(repository.forks_count));

        branchsTV.setText(repository.default_branch);
        branchCommitTV.setText("Latest commit by frodoking 7 days ago");
    }

    @Override
    public void onShowOrHide(boolean isShown) {
        getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
    }
}

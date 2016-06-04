package com.frodo.github.business.repository;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Issue;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.business.AbstractUIView;
import com.frodo.github.view.BaseListViewAdapter;
import com.frodo.github.view.CardViewGroup;
import com.frodo.github.view.MaxHeightListView;
import com.frodo.github.view.OcticonView;
import com.mikepenz.iconics.utils.Utils;
import com.mikepenz.octicons_typeface_library.Octicons;

import java.text.DateFormat;
import java.util.List;

import us.feras.mdv.MarkdownView;

/**
 * Created by frodo on 2016/5/7.
 */
public class RepositoryView extends AbstractUIView {
    private TextView descriptionTV;
    private OcticonView privateOV;
    private OcticonView issuesCountOV;
    private OcticonView dateOV;
    private OcticonView languageOV;
    private OcticonView sizeOV;
    private OcticonView ownerOV;

    private OcticonView starOV;
    private OcticonView watchOV;

    private TextView stargazersTV;
    private TextView watchersTV;
    private TextView forksTV;

    private CardViewGroup branchsCVG;
    private OcticonView branchsOV;
    private TextView branchCommitTV;

    private CardViewGroup readmeCVG;
    private MarkdownView readmeMDV;


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

        starOV = (OcticonView) getRootView().findViewById(R.id.star_ov);
        watchOV = (OcticonView) getRootView().findViewById(R.id.watch_ov);

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
        initCardView(branchsCVG, Octicons.Icon.oct_git_branch, "master", Octicons.Icon.oct_chevron_down, null, null);
        branchsOV = (OcticonView) branchsCVG.getHeaderView().findViewById(R.id.title_ov);

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
    }

    private void initReadmeCardView() {
        readmeCVG = (CardViewGroup) getRootView().findViewById(R.id.readme_cvg);
        initCardView(readmeCVG, Octicons.Icon.oct_book, "README.md", null, null, "View all of README.md");

        LinearLayout ll = (LinearLayout) readmeCVG.getContentView();
        readmeMDV = new MarkdownView(getPresenter().getAndroidContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                Utils.convertDpToPx(getPresenter().getAndroidContext(), 300));
        ll.addView(readmeMDV, params);
    }

    private void initPulseCardView() {
        pulseCVG = (CardViewGroup) getRootView().findViewById(R.id.pulse_cvg);
        initCardView(pulseCVG, Octicons.Icon.oct_pulse, "Pulse", null, "Past week", "View Pulse");
    }

    private void initIssuesCardView() {
        issuesCVG = (CardViewGroup) getRootView().findViewById(R.id.issues_cvg);
        initCardView(issuesCVG, Octicons.Icon.oct_issue_opened, "Issues", null, null, "View all issues");
    }

    private void initPullRequestsCardView() {
        pullRequestsCVG = (CardViewGroup) getRootView().findViewById(R.id.pull_requests_cvg);
        initCardView(pullRequestsCVG, Octicons.Icon.oct_git_pull_request, "Pull Requests", null, null, "View all pull requests");
    }

    private void initNotificationsCardView() {
        notificationsCVG = (CardViewGroup) getRootView().findViewById(R.id.notifications_cvg);
        initCardView(notificationsCVG, Octicons.Icon.oct_bell, "Notifications", null, null, null);
    }

    private void initCardView(CardViewGroup cvg, Octicons.Icon titleIcon, String titleText, Octicons.Icon subtitleIcon, String subtitleText, String footText) {
        OcticonView titleOV = (OcticonView) cvg.getHeaderView().findViewById(R.id.title_ov);
        if (titleIcon != null)
            titleOV.getFrescoAndIconicsImageView().setIcon(titleIcon);
        if (!TextUtils.isEmpty(titleText)) {
            titleOV.setText(titleText);
            titleOV.setTextColorRes(android.R.color.black);
        }

        OcticonView subtitleOV = (OcticonView) cvg.getHeaderView().findViewById(R.id.subtitle_ov);
        if (subtitleIcon != null)
            subtitleOV.getFrescoAndIconicsImageView().setIcon(subtitleIcon);
        if (!TextUtils.isEmpty(subtitleText))
            subtitleOV.setText(subtitleText);

        if (cvg.getFooterView() != null) {
            TextView footTV = (TextView) cvg.getFooterView().findViewById(R.id.text_tv);
            if (footText != null)
                footTV.setText(footText);
        }
    }

    @Override
    public void registerListener() {
    }

    public void showDetail(Repo repository) {
        descriptionTV.setText(repository.description);
        issuesCountOV.setText(String.format("%s issues", repository.open_issues_count));
        languageOV.setText(repository.language);
        sizeOV.setText(String.format("%s KB", repository.size));
        if (repository.created_at != null) {
            dateOV.setText(DateFormat.getDateTimeInstance().format(repository.created_at));
        }
        ownerOV.setText(repository.owner.login);
        privateOV.setText(repository.isPrivate ? "Private" : "Public");

        stargazersTV.setText(String.valueOf(repository.stargazers_count));
        watchersTV.setText(String.valueOf(repository.subscribers_count));
        forksTV.setText(String.valueOf(repository.forks_count));

        branchsOV.setText(repository.default_branch);
        branchCommitTV.setText("Latest commit by frodoking 7 days ago");
    }

    public void showReadme(String readmeContents) {
        readmeMDV.loadMarkdown(readmeContents);
    }

    public void showIssues(List<Issue> issues) {
        LinearLayout ll = (LinearLayout) issuesCVG.getContentView();
        ll.removeAllViews();
        ListView issueListView = new MaxHeightListView(getPresenter().getAndroidContext());
        ll.addView(issueListView);

        BaseListViewAdapter adapter = new IssuesForListViewAdapter(getPresenter().getAndroidContext());
        issueListView.setAdapter(adapter);
        adapter.refreshObjects(issues);
    }

    @Override
    public void onShowOrHide(boolean isShown) {
        getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
    }
}

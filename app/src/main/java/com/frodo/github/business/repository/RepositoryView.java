package com.frodo.github.business.repository;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.app.android.ui.FragmentScheduler;
import com.frodo.app.android.ui.activity.FragmentContainerActivity;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.MainActivity;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.Content;
import com.frodo.github.bean.dto.response.Issue;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.business.AbstractUIView;
import com.frodo.github.business.user.ProfileFragment;
import com.frodo.github.business.user.UserListFragment;
import com.frodo.github.view.BaseListViewAdapter;
import com.frodo.github.view.CardViewGroup;
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

    private LinearLayout.LayoutParams emptyLayoutParams;

    private Repo repo;

    public RepositoryView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.fragment_repository);
        emptyLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ResourceManager.getDimensionPixelSize(R.dimen.item_height_default));
        int margin = ResourceManager.getDimensionPixelSize(R.dimen.margin_middle);
        emptyLayoutParams.leftMargin = margin;
        emptyLayoutParams.rightMargin = margin;
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
        ll.addView(branchCommitTV, emptyLayoutParams);

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
        branchsCVG.getFooterView().findViewById(R.id.repo_view_code_ov).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString("repo", repo.owner.login + "/" + repo.name);
                FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), RepositoryContentsDirectoryFragment.class, arguments);
            }
        });

        stargazersTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString("users_args", String.format("users_repo_stargazers_%s_%s", repo.owner.login, repo.name));
                FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), UserListFragment.class, arguments);
            }
        });

        watchersTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString("users_args", String.format("users_repo_watchers_%s_%s", repo.owner.login, repo.name));
                FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), UserListFragment.class, arguments);
            }
        });

        forksTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString("repos_args", String.format("repos_forks_%s_%s", repo.owner.login, repo.name));
                FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), RepositoryListFragment.class, arguments);
            }
        });

        issuesCVG.getFooterView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString("issues_args", String.format("issues_repo_%s_%s", repo.owner.login, repo.name));
                FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), RepositoryIssuesFragment.class, arguments);
            }
        });
    }

    public void showDetail(final Repo repository) {
        this.repo = repository;

        if (!TextUtils.isEmpty(repository.description))
            descriptionTV.setText(repository.description);
        issuesCountOV.setText(String.format("%s issues", repository.open_issues_count));
        if (!TextUtils.isEmpty(repository.language))
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

        if (!TextUtils.isEmpty(repository.default_branch))
            branchsOV.setText(repository.default_branch);
        branchCommitTV.setText("Latest commit by frodoking 7 days ago");

        ((View)ownerOV.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString("username", repository.owner.login);
                FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), ProfileFragment.class, arguments);
            }
        });
    }

    public void showReadme(final Content content) {
        if (content.encoding.equalsIgnoreCase("base64")) {
            readmeMDV.loadMarkdown(new String(Base64.decode(content.content, Base64.DEFAULT)));
        }
        readmeCVG.getFooterView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putParcelable("content", content);
                FragmentScheduler.nextFragment((FragmentContainerActivity) getPresenter().getAndroidContext(), RepositoryContentsFileFragment.class, arguments);
            }
        });
    }

    public void showPulse(int closedPullsCount, int openedPullsCount, int closedIssuesCount, int openedIssuesCount) {
        LinearLayout ll = (LinearLayout) pulseCVG.getContentView();
        if (closedPullsCount == 0 && openedPullsCount == 0 &&
                closedIssuesCount == 0 && openedIssuesCount == 0) {
            ll.removeAllViews();
            TextView textView = new TextView(getPresenter().getAndroidContext());
            textView.setText("There haven’t been any recent conversations.");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            ll.addView(textView, emptyLayoutParams);
            return;
        }

        if (ll.findViewById(R.id.pulse_ll) == null) {
            ll.removeAllViews();
            LayoutInflater.from(getPresenter().getAndroidContext()).inflate(R.layout.view_repository_pulse, ll);
        }

        View pullsView = ll.findViewById(R.id.pull_requests_ll);
        fillPulesItem(pullsView,
                "Pull Requests",
                ResourceManager.getDrawable(R.drawable.progressbar_horizontal_pulse_pulls),
                closedPullsCount, openedPullsCount,
                Octicons.Icon.oct_git_pull_request, Octicons.Icon.oct_git_branch,
                "Merged PRs", "Proposed PRs");

        View issuesView = ll.findViewById(R.id.issues_ll);
        fillPulesItem(issuesView, "Issues",
                ResourceManager.getDrawable(R.drawable.progressbar_horizontal_pulse_issues),
                closedIssuesCount, openedIssuesCount,
                Octicons.Icon.oct_issue_closed, Octicons.Icon.oct_issue_opened,
                "Closed issues", "New issues");
    }

    private void fillPulesItem(View itemView, String titleText, Drawable progressDrawable,
                               float startSize, float endSize,
                               Octicons.Icon startIcon, Octicons.Icon endIcon,
                               String startText, String endText) {
        if (startSize == 0 && endSize == 0) {
            itemView.setVisibility(View.GONE);
        } else {
            ((TextView) itemView.findViewById(R.id.title_tv)).setText(titleText);
            ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.pb);
            progressBar.setProgressDrawable(progressDrawable);
            progressBar.setMax(100);
            progressBar.setProgress(endSize == 0 ? 100 : (int) (startSize / (startSize+endSize) * 100));

            View firstView = itemView.findViewById(R.id.first_ll);
            OcticonView startVO = (OcticonView) firstView.findViewById(R.id.title_ov);
            startVO.getFrescoAndIconicsImageView().setIcon(startIcon);
            startVO.setText(String.valueOf((int) startSize));
            ((TextView) firstView.findViewById(R.id.text_tv)).setText(startText);

            View secondView = itemView.findViewById(R.id.second_ll);
            OcticonView endVO = (OcticonView) secondView.findViewById(R.id.title_ov);
            endVO.getFrescoAndIconicsImageView().setIcon(endIcon);
            endVO.setText(String.valueOf((int) endSize));
            ((TextView) secondView.findViewById(R.id.text_tv)).setText(endText);
            itemView.setVisibility(View.VISIBLE);
        }
    }

    public void showIssues(List<Issue> issues) {
        LinearLayout ll = (LinearLayout) issuesCVG.getContentView();
        ll.removeAllViews();
        if (issues == null || issues.isEmpty()) {
            TextView textView = new TextView(getPresenter().getAndroidContext());
            textView.setText("There are no recent issues.");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            ll.addView(textView, emptyLayoutParams);
        } else {
            ListView issueListView =  (ListView) View.inflate(getPresenter().getAndroidContext(), R.layout.view_maxheight_listview, null);
            ll.addView(issueListView);

            BaseListViewAdapter adapter = new IssuesForListViewAdapter(getPresenter().getAndroidContext());
            issueListView.setAdapter(adapter);
            adapter.refreshObjects(issues);

            issuesCVG.getFooterView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
        }
    }

    public void showPullRequests(List<Issue> pullRequests) {
        LinearLayout ll = (LinearLayout) pullRequestsCVG.getContentView();
        ll.removeAllViews();
        if (pullRequests == null || pullRequests.isEmpty()) {
            TextView textView = new TextView(getPresenter().getAndroidContext());
            textView.setText("There are no recent pull requests.");
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setGravity(Gravity.CENTER_VERTICAL);
            ll.addView(textView, emptyLayoutParams);
        } else {
            ListView issueListView = (ListView) View.inflate(getPresenter().getAndroidContext(), R.layout.view_maxheight_listview, null);
            ll.addView(issueListView);

            BaseListViewAdapter adapter = new IssuesForListViewAdapter(getPresenter().getAndroidContext());
            issueListView.setAdapter(adapter);
            adapter.refreshObjects(pullRequests);

            pullRequestsCVG.getFooterView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //
                }
            });
        }
    }

    public void showNotifications(boolean isLogin, String type) {
        if (isLogin) {
            notificationsCVG.setVisibility(View.VISIBLE);
        } else {
            notificationsCVG.setVisibility(View.GONE);
        }
    }

    @Override
    public void onShowOrHide(boolean isShown) {
        getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
    }
}

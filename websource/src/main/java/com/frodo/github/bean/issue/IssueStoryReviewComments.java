package com.frodo.github.bean.issue;

import android.util.Pair;

import com.frodo.github.bean.dto.response.ReviewComment;
import com.frodo.github.bean.dto.response.User;

import java.util.List;

public class IssueStoryReviewComments implements IssueStoryDetail {

    public final Pair<String, List<ReviewComment>> event;
    private final long time;
    private final User user;

    public IssueStoryReviewComments(Pair<String, List<ReviewComment>> event, long time, User user) {
        this.event = event;

        this.time = time;
        this.user = user;
    }

    @Override
    public boolean isList() {
        return true;
    }

    @Override
    public String getType() {
        return "review_comments";
    }

    @Override
    public long createdAt() {
        return time;
    }

    @Override
    public User user() {
        return user;
    }
}

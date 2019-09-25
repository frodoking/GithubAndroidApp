package com.frodo.github.common;

import android.support.v4.util.Pair;

import com.frodo.app.framework.net.Request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by frodo on 2016/4/30.
 */
public final class Path
{
    public static final String HOST_GITHUB = "https://api.github.com";

    public static final String HOST_GITHUB_WEB = "http://github.com";

    public static final String HOST_CODEHUB = "http://trending.codehub-app.com";

    public static String replace(String url, Pair<String, String>... pairs)
    {
        for (Pair<String, String> pair : pairs)
        {
            if (url.contains("{"))
            {
                url = url.replace("{" + pair.first + "}", pair.second);
            }
            else if (url.contains(":"))
            {
				url = url.replace(":" + pair.first, pair.second);
            }
        }

        return url;
    }

    public static void warpRequestMethodAddQueryParam(Request request, String key, String value)
    {
        Class<?> requestClass = request.getClass();

        try
        {
            Method method = requestClass
                    .getDeclaredMethod("addQueryParam", String.class, String.class, boolean.class, boolean.class);
            method.setAccessible(true);
            method.invoke(request, key, value, false, false);
        }
        catch (NoSuchMethodException e)
        {
            e.printStackTrace();
        }
        catch (InvocationTargetException e)
        {
            e.printStackTrace();
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public final static class Page
    {
        public static final int PER_PAGE = 20;
    }

    public final static class Explore
    {
        public static final String SHOWCASES = "/v2/showcases";

        public static final String TRENDING = "/v2/trending";

        public static final String LANGUAGES = "/v2/languages";
    }

    public final static class Authentication
    {
        public static final String OAUTH_ACCESS_TOKEN = "/login/oauth/access_token";

        public static final String AUTHORIZATIONS = "/authorizations";
    }

    public final static class Activity
    {

        public final static class Events
        {
            public static final String LIST_PUBLIC_EVENTS = "/events";

            public static final String LIST_REPOSITORY_EVENTS = "/repos/{owner}/{repo}/events";

            public static final String LIST_ISSUE_EVENTS_FOR_A_REPOSITORY = "/repos/{owner}/{repo}/issues/events";

            public static final String LIST_PUBLIC_EVENTS_FOR_A_NETWORK_OF_REPOSITORIES =
                    "/networks/{owner}/{repo}/events";

            public static final String LIST_PUBLIC_EVENTS_FOR_AN_ORGANIZATION = "/orgs/:org/events";

            public static final String LIST_EVENTS_THAT_A_USER_HAS_RECEIVED = "/users/:username/received_events";

            public static final String LIST_PUBLIC_EVENTS_THAT_A_USER_HAS_RECEIVED =
                    "/users/:username/received_events/public";

            public static final String LIST_EVENTS_PERFORMED_BY_A_USER = "/users/:username/events";

            public static final String LIST_PUBLIC_EVENTS_PERFORMED_BY_A_USER = "/users/:username/events/public";

            public static final String LIST_EVENTS_FOR_AN_ORGANIZATION = "/users/:username/events/orgs/:org";
        }

        /**
         * https://developer.github.com/v3/activity/notifications/
         */
        public final static class Notifications
        {
            public static final String LIST_YOUR_NOTIFICATIONS = "/notifications";

            public static final String LIST_YOUR_NOTIFICATIONS_IN_A_REPOSITORY = "/repos/:owner/:repo/notifications";

            public static final String MARK_AS_READ = "/notifications";

            public static final String MARK_NOTIFICATIONS_AS_READ_IN_A_REPOSITORY = "/repos/:owner/:repo/notifications";

            public static final String VIEW_A_SINGLE_THREAD = "/notifications/threads/:thread_id";

            public static final String MARK_A_THREAD_AS_READ = "/notifications/threads/:thread_id";

            public static final String GET_A_THREAD_SUBSCRIPTION = "/notifications/threads/:thread_id/subscription";

            public static final String SET_A_THREAD_SUBSCRIPTION = "/notifications/threads/:thread_id/subscription";

            public static final String DELETE_A_THREAD_SUBSCRIPTION = "/notifications/threads/:thread_id/subscription";
        }
    }

    public final static class Gists
    {
        /**
         * https://developer.github.com/v3/gists/comments/
         */
        public final static class Comments
        {
            public static final String LIST_COMMENTS_ON_A_GIST = "/gists/:gist_id/comments";

            public static final String GET_A_SINGLE_COMMENT = "/gists/:gist_id/comments/:comment_id";

            public static final String CREATE_A_COMMENT = "/gists/:gist_id/comments";

            public static final String EDIT_A_COMMENT = "/gists/:gist_id/comments/:comment_id";

            public static final String DELETE_A_COMMENT = "/gists/:gist_id/comments/:comment_id";
        }
    }

    public final static class GitData
    {
        public static final String GIT_TREE = Repositories.REPOS_FULLNAME + "/git/trees/{sha}";
    }

    /**
     * https://developer.github.com/v3/issues/
     */
    public final static class Issues
    {
        /**
         * https://developer.github.com/v3/issues/assignees/
         */
        public final static class Assignees
        {
            public static final String LIST_CURRENT_USER_ISSUES = "/issues";

            public static final String LIST_USER_ISSUES = "/user/issues";

            public static final String LIST_ORG_ISSUES = "/orgs/:org/issues";

            public static final String LIST_ISSUES_FOR_A_REPOSITORY = "/repos/:owner/:repo/issues";

            public static final String GET_A_SINGLE_ISSUE = "/repos/:owner/:repo/issues/:issue_number";

            public static final String CREATE_AN_ISSUE = "/repos/:owner/:repo/issues";

            public static final String EDIT_AN_ISSUE = "/repos/:owner/:repo/issues/:issue_number";

            public static final String LOCK_AN_ISSUE = "/repos/:owner/:repo/issues/:issue_number/lock";

            public static final String UNLOCK_AN_ISSUE = "/repos/:owner/:repo/issues/:issue_number/lock";
        }
    }

    /**
     * https://developer.github.com/v3/orgs/
     */
    public final static class Organizations
    {
        public static final String LIST_YOUR_ORGANIZATIONS = "/user/orgs";

        public static final String LIST_ALL_ORGANIZATIONS = "/organizations";

        public static final String LIST_USER_ORGANIZATIONS = "/users/:username/orgs";

        public static final String GET_AN_ORGANIZATION = "/orgs/:org";

        public static final String EDIT_AN_ORGANIZATION = "/orgs/:org";

        public static final String LIST_CREDENTIAL_AUTHORIZATIONS_FOR_AN_ORGANIZATION =
                "/orgs/:org/credential-authorizations";

        public static final String REMOVE_A_CREDENTIAL_AUTHORIZATION_FOR_AN_ORGANIZATION =
                "/orgs/:org/credential-authorizations/:credential_id";
    }

    /**
     * https://developer.github.com/v3/pulls/#labels-assignees-and-milestones
     */
    public static class PullRequests
    {
        public static final String LIST_PULL_REQUESTS = "/repos/:owner/:repo/pulls";

        public static final String GET_A_SINGLE_PULL_REQUEST = "/repos/:owner/:repo/pulls/:pull_number";

        public static final String CREATE_A_PULL_REQUEST = "/repos/:owner/:repo/pulls";

        public static final String UPDATE_A_PULL_REQUEST_BRANCH =
                "/repos/:owner/:repo/pulls/:pull_number/update-branch";

        public static final String UPDATE_A_PULL_REQUEST = "/repos/:owner/:repo/pulls/:pull_number";

        public static final String LIST_COMMITS_ON_A_PULL_REQUEST = "/repos/:owner/:repo/pulls/:pull_number/commits";

        public static final String LIST_PULL_REQUESTS_FILES = "/repos/:owner/:repo/pulls/:pull_number/files";

        public static final String GET_IF_A_PULL_REQUEST_HAS_BEEN_MERGED =
                "/repos/:owner/:repo/pulls/:pull_number/merge";

        public static final String MERGE_A_PULL_REQUEST = "/repos/:owner/:repo/pulls/:pull_number/merge";

        /**
         * https://developer.github.com/v3/pulls/#labels-assignees-and-milestones
         */
        public static class Reviews
        {
            public static final String LIST_REVIEWS_ON_A_PULL_REQUEST =
                    "/repos/:owner/:repo/pulls/:pull_number/reviews";

            public static final String GET_A_SINGLE_REVIEW =
                    "/repos/:owner/:repo/pulls/:pull_number/reviews/:review_id";

            public static final String DELETE_A_PENDING_REVIEW =
                    "/repos/:owner/:repo/pulls/:pull_number/reviews/:review_id";

            public static final String GET_COMMENTS_FOR_A_SINGLE_REVIEW =
                    "/repos/:owner/:repo/pulls/:pull_number/reviews/:review_id/comments";

            public static final String CREATE_A_PULL_REQUEST_REVIEW = "/repos/:owner/:repo/pulls/:pull_number/reviews";

            public static final String UPDATE_A_PULL_REQUEST_REVIEW =
                    "/repos/:owner/:repo/pulls/:pull_number/reviews/:review_id";

            public static final String SUBMIT_A_PULL_REQUEST_REVIEW =
                    "/repos/:owner/:repo/pulls/:pull_number/reviews/:review_id/events";

            public static final String DISMISS_A_PULL_REQUEST_REVIEW =
                    "/repos/:owner/:repo/pulls/:pull_number/reviews/:review_id/dismissals";
        }
    }

    public final static class Repositories
    {
        public static final String REPOS_FULLNAME = "/repos/{owner}/{repo}";

        public static final String REPOS_README = REPOS_FULLNAME + "/readme";

        public static final String REPOS_CONTENTS = REPOS_FULLNAME + "/contents/{path}";

        public static final String REPOS_LABELS = REPOS_FULLNAME + "/labels";

        public static final String REPOS_ISSUES = REPOS_FULLNAME + "/issues";

        public static final String REPOS_ISSUES_NUMBER = REPOS_ISSUES + "/{number}";

        public static final String REPOS_ISSUES_NUMBER_COMMENTS = REPOS_ISSUES_NUMBER + "/comments";

        public static final String REPOS_PULLS = REPOS_FULLNAME + "/pulls";

        public static final String REPOS_STARGAZERS = REPOS_FULLNAME + "/stargazers";

        public static final String REPOS_WATCHERS = REPOS_FULLNAME + "/watchers";

        public static final String REPOS_FORKS = REPOS_FULLNAME + "/forks";
    }

    public final static class Search
    {

        public static final String REPOS = "/search/repositories";

        public static final String COMMITS = "/search/commits";

        public static final String CODE = "/search/code";

        public static final String ISSUES_AND_PULL_REQUESTS = "/search/issues";

        public static final String USERS = "/search/users";

        public static final String TOPICS = "/search/topics";

        public static final String LABELS = "/search/labels";

    }

    public final static class Users
    {
        /**
         * "/users/{username}"
         */
        public static final String USER = "/users/{username}";

        /**
         * List followers of a user
         */
        public static final String USER_FOLLOWERS = USER + "/followers";

        /**
         * List users followed by another user
         */
        public static final String USER_FOLLOWING = USER + "/following";

        /**
         * Repositories
         */
        public static final String USER_REPOS = USER + "/repos";

        /**
         * authenticated user
         */
        public static final String USER_ISSUES = "/issues";

        /**
         * do follow or unfollow
         */
        public static final String USER_FOLLOW = "/user/following/{username}";
    }
}

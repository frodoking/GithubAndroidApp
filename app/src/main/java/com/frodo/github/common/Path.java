package com.frodo.github.common;

/**
 * Created by frodo on 2016/4/30.
 */
public final class Path {
    public static final String HOST_GITHUB = "https://api.github.com";
    public static final String HOST_CODEHUB = "http://trending.codehub-app.com";

    public final static class Explore {
        public static final String V2_SHOWCASES = "/v2/showcases";
        public static final String V2_TRENDING_REPOSITORIES = "/v2/trending";
    }

    public final static class Commits {

    }

    public final static class Content {

    }

    public final static class Emojis {

    }

    public final static class Gists {

    }

    public final static class GitData {

    }

    public final static class GitIgnore {

    }

    public final static class Issues {

    }

    public final static class Login {
        public static final String requestToken = "/login/oauth/access_token";
        public static final String authorizations = "/authorizations";
    }

    public final static class Notifications {

    }

    public final static class Orgs {

    }

    public static class PullRequests {
        public static final String pulls = "/repos/{owner}/{name}/pulls";
        public static final String pull = "/repos/{owner}/{name}/pulls/{number}";
    }

    public final static class Repo {

    }

    public final static class Repos {

    }

    public final static class Search {

    }

    public final static class Users {

    }

    public final static class WebHook {

    }
}
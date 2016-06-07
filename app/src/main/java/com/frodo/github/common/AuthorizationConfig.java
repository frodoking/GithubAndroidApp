package com.frodo.github.common;

/**
 * Created by frodo on 2016/6/7.
 */
public class AuthorizationConfig {
    public static final String[] SCOPES = new String[]{
            Scope.ADMIN_ORG, Scope.ADMIN_ORG_HOOK, Scope.ADMIN_PUBLIC_KEY, Scope.ADMIN_REPO_HOOK,
            Scope.DELETE_REPO,
            Scope.GIST,
            Scope.NOTIFICATIONS,
            Scope.REPO,
            Scope.USER,
    };
    public static final String NOTE = "GithubAndroidClient";
    public static final String NOTE_URL = "";
    public static final String CLIENT_ID = "xxx";
    public static final String CLIENT_SECRET = "xxx";
    public static final String FINGERPRINT = "";
}

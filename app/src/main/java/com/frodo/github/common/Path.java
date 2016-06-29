package com.frodo.github.common;

import android.support.v4.util.Pair;

import com.frodo.app.framework.net.Request;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by frodo on 2016/4/30.
 */
public final class Path {
	public static final String HOST_GITHUB = "https://api.github.com";
	public static final String HOST_GITHUB_WEB = "http://github.com";
	public static final String HOST_CODEHUB = "http://trending.codehub-app.com";

	public static String replace(String url, Pair<String, String>... pairs) {
		for (Pair<String, String> pair : pairs) {
			url = url.replace("{" + pair.first + "}", pair.second);
		}

		return url;
	}

	public static void warpRequestMethodAddQueryParam(Request request, String key, String value) {
		Class<?> requestClass = request.getClass();

		try {
			Method method = requestClass.getDeclaredMethod("addQueryParam", String.class, String.class, boolean.class, boolean.class);
			method.setAccessible(true);
			method.invoke(request, key, value, false, false);
		} catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public final static class Page {
		public static final int PER_PAGE = 20;
	}

	public final static class Explore {
		public static final String SHOWCASES = "/v2/showcases";
		public static final String TRENDING = "/v2/trending";
		public static final String LANGUAGES = "/v2/languages";
	}

	public final static class Authentication {
		public static final String OAUTH_ACCESS_TOKEN = "/login/oauth/access_token";
		public static final String AUTHORIZATIONS = "/authorizations";
	}

	public final static class Activity {
		public static final String RECEIVED_EVENTS = "/users/{username}/received_events";
		public static final String USER_EVENTS = "/users/{username}/events";
		public static final String NOTIFICATIONS = "/notifications";
		public static final String EVENTS = "/events";
	}

	public final static class Gists {

	}

	public final static class GitData {
		public static final String GIT_TREE = Repositories.REPOS_FULLNAME + "/git/trees/{sha}";
	}

	public final static class Issues {

	}

	public final static class Organizations {

	}

	public static class PullRequests {
		public static final String REPOS_OWNER_NAME_PULLS = "/repos/{owner}/{name}/pulls";
		public static final String REPOS_OWNER_NAME_PULLS_NUMBER = "/repos/{owner}/{name}/pulls/{number}";
	}

	public final static class Repositories {
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

	public final static class Search {
		public static final String USERS = "/search/users";
		public static final String REPOS = "/search/repositories";
		public static final String ISSUES = "/search/issues";
		public static final String CODE = "/search/code";
	}

	public final static class Users {
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

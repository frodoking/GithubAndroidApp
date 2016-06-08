package com.frodo.github.business.repository;


import android.support.v4.util.Pair;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.net.Response;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.bean.dto.response.Content;
import com.frodo.github.bean.dto.response.GitBlob;
import com.frodo.github.bean.dto.response.GitTree;
import com.frodo.github.bean.dto.response.Issue;
import com.frodo.github.bean.dto.response.Label;
import com.frodo.github.bean.dto.response.PullRequest;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.business.user.UserModel;
import com.frodo.github.common.Path;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by frodo on 2016/5/7.
 */
public class RepositoryModel extends AbstractModel {
    public static final String TAG = RepositoryModel.class.getSimpleName();

    private AndroidFetchNetworkDataTask fetchRepositoryNetworkDataTask;
    private AndroidFetchNetworkDataTask fetchRepositoryFileNetworkDataTask;

    private AndroidFetchNetworkDataTask fetchAllLabelsFileNetworkDataTask;
    private AndroidFetchNetworkDataTask fetchIssuesFileNetworkDataTask;
    private AndroidFetchNetworkDataTask fetchPullsFileNetworkDataTask;

    private AndroidFetchNetworkDataTask fetchGitTreeNetworkDataTask;

    public RepositoryModel(MainController controller) {
        super(controller);
    }

    @Override
    public void initBusiness() {
    }


    @Override
    public String name() {
        return TAG;
    }

    public Observable<Repo> loadRepositoryDetailWithReactor(final String ownerName, final String repoName) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(Path.replace(Path.Repositories.REPOS_FULLNAME, new Pair<>("owner", ownerName), new Pair<>("repo", repoName)))
                        .build();
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                fetchRepositoryNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(fetchRepositoryNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<Repo>>() {
            @Override
            public Observable<Repo> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    Repo repo = JsonConverter.convert(rb.string(), Repo.class);
                    return Observable.just(repo);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    public Observable<List<Repo>> loadUsersRepos(String username) {
        UserModel userModel = getMainController().getModelFactory()
                .getOrCreateIfAbsent(UserModel.TAG, UserModel.class, getMainController());
        return userModel.loadRepositoriesWithReactor(username);
    }

    public Observable<List<Label>> loadAllLabelsWithReactor(final String ownerName, final String repoName) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(Path.replace(Path.Repositories.REPOS_LABELS, new Pair<>("owner", ownerName), new Pair<>("repo", repoName)))
                        .build();
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                fetchAllLabelsFileNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(fetchAllLabelsFileNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<List<Label>>>() {
            @Override
            public Observable<List<Label>> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    List<Label> labels = JsonConverter.convert(rb.string(), new TypeReference<List<Label>>() {
                    });
                    return Observable.just(labels);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    /**
     * https://api.github.com/repos/FreeCodeCamp/FreeCodeCamp/issues?state=open&since=2016-05-30T18:58:10Z
     */
    public Observable<List<Issue>> loadClosedIssuesInPastWeekWithReactor(final String ownerName, final String repoName) {
        return loadIssuesWithReactor(ownerName, repoName, "all", "closed", null, "comments", null, getPastWeek(), -1, -1);
    }

    public Observable<List<Issue>> loadOpendIssuesInPastWeekWithReactor(final String ownerName, final String repoName) {
        return loadIssuesWithReactor(ownerName, repoName, "all", "open", null, "comments", null, getPastWeek(), -1, -1);
    }

    public Observable<List<Issue>> loadRecentIssuesWithReactor(final String ownerName, final String repoName) {
        return loadIssuesWithReactor(ownerName, repoName, null, null, null, "comments", null, null, 0, 5);
    }

    /**
     * List Issues
     *
     * @param ownerName owner login
     * @param repoName  repo name
     * @param filter    Indicates which sorts of issues to return. Can be one of:
     *                  assigned: Issues assigned to you
     *                  created: Issues created by you
     *                  mentioned: Issues mentioning you
     *                  subscribed: Issues you're subscribed to updates for
     *                  all: All issues the authenticated user can see, regardless of participation or creation
     *                  Default: assigned
     * @param state     Indicates the state of the issues to return. Can be either open, closed, or all. Default: open
     * @param labels    A list of comma separated label names. Example: bug,ui,@high
     * @param sort      What to sort results by. Can be either created, updated, comments. Default: created
     * @param direction The direction of the sort. Can be either asc or desc. Default: desc
     * @param since     Only issues updated at or after this time are returned. This is a timestamp in ISO 8601 format: YYYY-MM-DDTHH:MM:SSZ.
     * @param page
     * @param perPage
     * @return Observable<List<Issue>>
     */
    public Observable<List<Issue>> loadIssuesWithReactor(final String ownerName, final String repoName,
                                                         final String filter,
                                                         final String state,
                                                         final String labels,
                                                         final String sort,
                                                         final String direction,
                                                         final String since,
                                                         final int page, final int perPage) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(Path.replace(Path.Repositories.REPOS_ISSUES, new Pair<>("owner", ownerName), new Pair<>("repo", repoName)))
                        .build();
                if (!TextUtils.isEmpty(filter)) {
                    request.addQueryParam("filter", filter);
                }
                if (!TextUtils.isEmpty(state)) {
                    request.addQueryParam("state", state);
                }
                if (!TextUtils.isEmpty(labels)) {
                    request.addQueryParam("labels", labels);
                }
                if (!TextUtils.isEmpty(sort)) {
                    request.addQueryParam("sort", sort);
                }
                if (!TextUtils.isEmpty(direction)) {
                    request.addQueryParam("direction", direction);
                }
                if (!TextUtils.isEmpty(since)) {
                    request.addQueryParam("since", since);
                }
                if (page != -1) {
                    request.addQueryParam("page", String.valueOf(page));
                }
                if (perPage != -1) {
                    request.addQueryParam("per_page", String.valueOf(perPage));
                }
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                fetchIssuesFileNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(fetchIssuesFileNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<List<Issue>>>() {
            @Override
            public Observable<List<Issue>> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    List<Issue> issues = JsonConverter.convert(rb.string(), new TypeReference<List<Issue>>() {
                    });
                    return Observable.just(issues);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    public Observable<List<PullRequest>> loadClosedPullsInPastWeekWithReactor(final String ownerName, final String repoName) {
        return loadPullsWithReactor(ownerName, repoName, "closed", null, null, "popularity", null, -1, -1);
    }

    public Observable<List<PullRequest>> loadOpenedPullsInPastWeekWithReactor(final String ownerName, final String repoName) {
        return loadPullsWithReactor(ownerName, repoName, "open", null, null, "popularity", null, -1, -1);
    }

    public Observable<List<PullRequest>> loadRecentPullsWithReactor(final String ownerName, final String repoName) {
        return loadPullsWithReactor(ownerName, repoName, null, null, null, "popularity", null, 0, 5);
    }

    /**
     * List pull requests
     *
     * @param ownerName owner login
     * @param repoName  repo name
     * @param state     Either open, closed, or all to filter by state. Default: open
     * @param head      Filter pulls by head user and branch name in the format of user:ref-name. Example: github:new-script-format.
     * @param base      Filter pulls by base branch name. Example: gh-pages.
     * @param sort      What to sort results by. Can be either created, updated, popularity (comment count) or long-running (age, filtering by pulls updated in the last month). Default: created
     * @param direction The direction of the sort. Can be either asc or desc. Default: desc when sort is created or sort is not specified, otherwise asc.
     * @param page
     * @param perPage
     * @return Observable<List<PullRequest>>
     */
    public Observable<List<PullRequest>> loadPullsWithReactor(final String ownerName, final String repoName,
                                                              final String state,
                                                              final String head,
                                                              final String base,
                                                              final String sort,
                                                              final String direction,
                                                              final int page, final int perPage) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(Path.replace(Path.Repositories.REPOS_PULLS, new Pair<>("owner", ownerName), new Pair<>("repo", repoName)))
                        .build();

                if (!TextUtils.isEmpty(state)) {
                    request.addQueryParam("state", state);
                }
                if (!TextUtils.isEmpty(head)) {
                    request.addQueryParam("head", head);
                }
                if (!TextUtils.isEmpty(base)) {
                    request.addQueryParam("base", base);
                }
                if (!TextUtils.isEmpty(sort)) {
                    request.addQueryParam("sort", sort);
                }
                if (!TextUtils.isEmpty(direction)) {
                    request.addQueryParam("direction", direction);
                }
                if (page != -1) {
                    request.addQueryParam("page", String.valueOf(page));
                }
                if (perPage != -1) {
                    request.addQueryParam("per_page", String.valueOf(perPage));
                }
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                fetchPullsFileNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(fetchPullsFileNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<List<PullRequest>>>() {
            @Override
            public Observable<List<PullRequest>> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    List<PullRequest> pulls = JsonConverter.convert(rb.string(), new TypeReference<List<PullRequest>>() {
                    });
                    return Observable.just(pulls);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    public static String getPastWeek() {
        Calendar cal = Calendar.getInstance();
        cal.add(java.util.Calendar.DATE, -7);

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
        df.setTimeZone(tz);
        return df.format(cal.getTime());
    }

    public Observable<Content> loadReadmeWithReactor(final String ownerName, final String repoName) {
        return loadContentWithReactor(Path.replace(Path.Repositories.REPOS_README,
                new Pair<>("owner", ownerName),
                new Pair<>("repo", repoName)), "master");
    }

    public Observable<Content> loadContentWithReactor(final String ownerName, final String repoName, final String path, final String ref) {
        return loadContentWithReactor(Path.replace(Path.Repositories.REPOS_CONTENTS,
                new Pair<>("owner", ownerName),
                new Pair<>("repo", repoName),
                new Pair<>("path", path)), ref);
    }

    public Observable<List<Content>> loadContentsWithReactor(final String ownerName, final String repoName, final String path, final String ref) {
        return loadContentsWithReactor(Path.replace(Path.Repositories.REPOS_CONTENTS,
                new Pair<>("owner", ownerName),
                new Pair<>("repo", repoName),
                new Pair<>("path", path)), ref);
    }

    private Observable<List<Content>> loadContentsWithReactor(final String path, final String ref) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(path)
                        .build();
                request.addQueryParam("ref", ref);
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                fetchGitTreeNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(fetchGitTreeNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<List<Content>>>() {
            @Override
            public Observable<List<Content>> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    List<Content> contents = JsonConverter.convert(rb.string(), new TypeReference<List<Content>>() {
                    });
                    return Observable.just(contents);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    public Observable<Content> loadContentWithReactor(final String path, final String ref) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(path)
                        .build();
                if (!TextUtils.isEmpty(ref))
                    request.addQueryParam("ref", ref);
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                fetchGitTreeNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(fetchGitTreeNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<Content>>() {
            @Override
            public Observable<Content> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    Content content = JsonConverter.convert(rb.string(), new TypeReference<Content>() {
                    });
                    return Observable.just(content);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }
}

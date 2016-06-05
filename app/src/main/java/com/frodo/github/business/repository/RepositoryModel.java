package com.frodo.github.business.repository;


import android.util.LruCache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.net.Response;
import com.frodo.github.bean.dto.response.GitBlob;
import com.frodo.github.bean.dto.response.Issue;
import com.frodo.github.bean.dto.response.Label;
import com.frodo.github.bean.dto.response.PullRequest;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.business.user.UserModel;
import com.frodo.github.common.Path;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by frodo on 2016/5/7.
 */
public class RepositoryModel extends AbstractModel {
    public static final String TAG = RepositoryModel.class.getSimpleName();

    private LruCache<String, List<Label>> lruLabelsCache = new LruCache<>(5);
    private AndroidFetchNetworkDataTask fetchRepositoryNetworkDataTask;
    private AndroidFetchNetworkDataTask fetchRepositoryFileNetworkDataTask;

    private AndroidFetchNetworkDataTask fetchAllLabelsFileNetworkDataTask;
    private AndroidFetchNetworkDataTask fetchIssuesFileNetworkDataTask;
    private AndroidFetchNetworkDataTask fetchPullsFileNetworkDataTask;

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

    public Observable<Repo> loadRepositoryDetailWithReactor(final String slug) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(String.format(Path.Repositories.REPOS_FULLNAME, slug))
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

    public Observable<GitBlob> loadFile(final String ownerName, final String repoName, final String fileName) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(String.format(Path.Repositories.REPOS_CONTENTS, ownerName, repoName, fileName))
                        .build();
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                fetchRepositoryFileNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(fetchRepositoryFileNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<GitBlob>>() {
            @Override
            public Observable<GitBlob> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    GitBlob blob = JsonConverter.convert(rb.string(), GitBlob.class);
                    return Observable.just(blob);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    public List<Label> getAllLables(final String ownerName, final String repoName) {
        return lruLabelsCache.get(ownerName + "/" + repoName);
    }

    public void putAllLables(final String ownerName, final String repoName, List<Label> labels) {
        lruLabelsCache.put(ownerName + "/" + repoName, labels);
    }

    public Observable<List<Label>> loadAllLabelsWithReactor(final String ownerName, final String repoName) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(String.format(Path.Repositories.REPOS_LABELS, ownerName, repoName))
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

    public Observable<List<Issue>> loadAllIssuesWithReactor(final String ownerName, final String repoName) {
        return loadIssuesWithReactor(ownerName, repoName, -1, -1);
    }

    public Observable<List<Issue>> loadIssuesWithReactor(final String ownerName, final String repoName, final int page, final int perPage) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(String.format(Path.Repositories.REPOS_ISSUES, ownerName, repoName))
                        .build();
                if (page != -1)
                    request.addQueryParam("page", String.valueOf(page));
                if (perPage != -1)
                    request.addQueryParam("per_page", String.valueOf(perPage));

                request.addQueryParam("state", "all");
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

    public Observable<List<PullRequest>> loadAllPullsWithReactor(final String ownerName, final String repoName) {
        return loadPullsWithReactor(ownerName, repoName, -1, -1);
    }

    public Observable<List<PullRequest>> loadPullsWithReactor(final String ownerName, final String repoName, final int page, final int perPage) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(String.format(Path.Repositories.REPOS_PULLS, ownerName, repoName))
                        .build();
                if (page != -1)
                    request.addQueryParam("page", String.valueOf(page));
                if (perPage != -1)
                    request.addQueryParam("per_page", String.valueOf(perPage));

                request.addQueryParam("state", "all");
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
}

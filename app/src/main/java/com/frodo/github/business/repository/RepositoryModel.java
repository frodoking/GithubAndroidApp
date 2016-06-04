package com.frodo.github.business.repository;

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
    private AndroidFetchNetworkDataTask fetchRepositoryNetworkDataTask;
    private AndroidFetchNetworkDataTask fetchRepositoryFileNetworkDataTask;
    private AndroidFetchNetworkDataTask fetchIssuesFileNetworkDataTask;

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

    public Observable<List<Issue>> loadIssuesDetailWithReactor(final String ownerName, final String repoName, final int page, final int perPage) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(String.format(Path.Repositories.REPOS_ISSUES, ownerName, repoName))
                        .build();
                request.addQueryParam("page", String.valueOf(page));
                request.addQueryParam("per_page", String.valueOf(perPage));
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
}

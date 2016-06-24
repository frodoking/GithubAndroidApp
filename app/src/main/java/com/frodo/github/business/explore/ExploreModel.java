package com.frodo.github.business.explore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.net.Response;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.bean.dto.response.search.ReposSearch;
import com.frodo.github.bean.dto.response.search.UsersSearch;
import com.frodo.github.business.repository.RepositoryModel;
import com.frodo.github.business.user.UserModel;
import com.frodo.github.common.Path;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by frodo on 2016/4/30.
 */
public class ExploreModel extends AbstractModel {
    private boolean enableCached;
    private List<ShowCase> showCases;

    private AndroidFetchNetworkDataTask fetchShowCasesNetworkDataTask;
    private AndroidFetchNetworkDataTask fetchRepositoriesNetworkDataTask;

    private ShowCaseListCache showCaseListCache;

    public ExploreModel(MainController controller) {
        super(controller);
    }

    public boolean isEnableCached() {
        return enableCached;
    }

    public void setEnableCached(boolean enableCached) {
        this.enableCached = enableCached;
        if (enableCached) {
            showCaseListCache = new ShowCaseListCache(getMainController().getCacheSystem());
        } else {
            showCaseListCache = null;
        }
    }

    public List<ShowCase> getShowCasesFromCache() {
        return showCaseListCache.get(ShowCaseListCache.CACHE_KEY);
    }

    public List<ShowCase> getShowCases() {
        return showCases;
    }

    public void setShowCases(List<ShowCase> showCases) {
        this.showCases = showCases;
        if (enableCached) {
            showCaseListCache.put(ShowCaseListCache.CACHE_KEY, showCases);
        }
    }

    public Observable<List<ShowCase>> loadShowCasesWithReactor() {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(Path.Explore.SHOWCASES)
                        .build();
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_CODEHUB);
                fetchShowCasesNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(fetchShowCasesNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<List<ShowCase>>>() {
            @Override
            public Observable<List<ShowCase>> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    List<ShowCase> showCases = JsonConverter.convert(rb.string(), new TypeReference<List<ShowCase>>() {
                    });
                    setShowCases(showCases);
                    return Observable.just(showCases);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    public Observable<List<Repo>> loadTrendingRepositoriesInWeeklyWithReactor() {
        return loadTrendingRepositoriesWithReactor("weekly", null);
    }

    public Observable<List<Repo>> loadTrendingRepositoriesWithReactor(final String since, final String language) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(Path.Explore.TRENDING)
                        .build();
                if (!TextUtils.isEmpty(since))
                    request.addQueryParam("since", since);
                if (!TextUtils.isEmpty(language))
                    request.addQueryParam("language", language);
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_CODEHUB);
                fetchRepositoriesNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(fetchRepositoriesNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<List<Repo>>>() {
            @Override
            public Observable<List<Repo>> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    List<Repo> repos = JsonConverter.convert(rb.string(), new TypeReference<List<Repo>>() {
                    });
                    return Observable.just(repos);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    public Observable<List<User>> loadTrendingDevelopersWithReactor(String since, String language) {
        String q = TextUtils.isEmpty(language) ? "language" : String.format("language:%s", language);
        final UserModel userModel = getMainController().getModelFactory().getOrCreateIfAbsent(UserModel.TAG, UserModel.class, getMainController());
        return userModel.searchUsers(q, "followers", null).map(new Func1<UsersSearch, List<User>>() {
            @Override
            public List<User> call(UsersSearch usersSearch) {
                return usersSearch.items;
            }
        });
    }

    public Observable<List<Repo>> loadTrendingRepositoriesWithReactor2(String since, String language) {
        String q = TextUtils.isEmpty(language) ? "language" : String.format("language:%s", language);
        RepositoryModel repositoryModel = getMainController().getModelFactory().getOrCreateIfAbsent(RepositoryModel.TAG, RepositoryModel.class, getMainController());
        return repositoryModel.searchRepos(q, "stars", null).map(new Func1<ReposSearch, List<Repo>>() {
            @Override
            public List<Repo> call(ReposSearch reposSearch) {
                return reposSearch.items;
            }
        });
    }
}

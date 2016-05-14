package com.frodo.github.business.explore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.cache.Cache;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.github.bean.Repository;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.business.showcases.ShowCaseListCache;
import com.frodo.github.common.Path;

import java.util.List;

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
    private ShowCaseListCache showCaseListCache;

    private AndroidFetchNetworkDataTask fetchRepositoriesNetworkDataTask;

    public ExploreModel(MainController controller) {
        super(controller);
        if (enableCached) {
            showCaseListCache = new ShowCaseListCache(getMainController().getCacheSystem(), Cache.Type.DISK);
        }
    }

    @Override
    public void initBusiness() {
    }

    public List<ShowCase> getShowCases() {
        return showCases;
    }

    public void setShowCases(List<ShowCase> showCases) {
        this.showCases = showCases;
        if (enableCached) {
            showCaseListCache.put(fetchShowCasesNetworkDataTask.key(), showCases);
        }
    }

    public Observable<List<ShowCase>> loadShowCasesWithReactor() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Request request = new Request("GET", Path.Explore.SHOWCASES);
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_CODEHUB);
                fetchShowCasesNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, (Subscriber<String>) subscriber);
                getMainController().getBackgroundExecutor().execute(fetchShowCasesNetworkDataTask);
            }
        }).flatMap(new Func1<String, Observable<List<ShowCase>>>() {
            @Override
            public Observable<List<ShowCase>> call(final String s) {
                return Observable.create(new Observable.OnSubscribe<List<ShowCase>>() {
                    @Override
                    public void call(Subscriber<? super List<ShowCase>> subscriber) {
                        List<ShowCase> showCases = JsonConverter.convert(s, new TypeReference<List<ShowCase>>() {
                        });
                        subscriber.onNext(showCases);
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    public Observable<List<Repository>> loadRepositoriesWithReactor() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Request request = new Request("GET", Path.Explore.TRENDING);
                request.addQueryParam("since", "weekly");
                request.addQueryParam("language", "");
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_CODEHUB);
                fetchRepositoriesNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, (Subscriber<String>) subscriber);
                getMainController().getBackgroundExecutor().execute(fetchRepositoriesNetworkDataTask);
            }
        }).flatMap(new Func1<String, Observable<List<Repository>>>() {
            @Override
            public Observable<List<Repository>> call(final String s) {
                return Observable.create(new Observable.OnSubscribe<List<Repository>>() {
                    @Override
                    public void call(Subscriber<? super List<Repository>> subscriber) {
                        List<Repository> repositories = JsonConverter.convert(s, new TypeReference<List<Repository>>() {
                        });
                        subscriber.onNext(repositories);
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }

    public boolean isEnableCached() {
        return enableCached;
    }

    public void setEnableCached(boolean enableCached) {
        this.enableCached = enableCached;
    }

    public List<ShowCase> getShowCasesFromCache() {
        return showCases;
    }
}

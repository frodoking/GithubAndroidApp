package com.frodo.github.business.explore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.cache.Cache;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.common.Path;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import rx.Subscriber;

/**
 * Created by frodo on 2016/4/30.
 */
public class ExploreModel extends AbstractModel {
    private boolean enableCached;
    private List<ShowCase> showCases;
    private List<ShowCase> showCasesFromCache;

    private AndroidFetchNetworkDataTask fetchShowCasesNetworkDataTask;
    private ShowCaseListCache showCaseListCache;

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

    public void setEnableCached(boolean enableCached) {
        this.enableCached = enableCached;
    }

    public void loadShowCasesWithReactor(final Subscriber<? super List<ShowCase>> subscriber) {
        Request request = new Request("GET", Path.v2_showcases);
        final NetworkTransport networkTransport = getMainController().getNetworkTransport();
        networkTransport.setAPIUrl("http://trending.codehub-app.com");
        fetchShowCasesNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, new Subscriber<String>() {

            @Override
            public void onStart() {
                super.onStart();
                subscriber.onStart();
            }

            @Override
            public void onCompleted() {
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onNext(String s) {
                final String listString = s;
                try {
                    new JSONArray(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                    return;
                }

                if (listString != null) {
                    List<ShowCase> showCases = JsonConverter.convert(listString, new TypeReference<List<ShowCase>>() {
                    });
                    subscriber.onNext(showCases);
                } else {
                    subscriber.onNext(null);
                }
            }
        });
        getMainController().getBackgroundExecutor().execute(fetchShowCasesNetworkDataTask);
    }

    public void setShowCases(List<ShowCase> showCases) {
        this.showCases = showCases;
        if (enableCached) {
            showCaseListCache.put(fetchShowCasesNetworkDataTask.key(), showCases);
        }
    }

    public boolean isEnableCached() {
        return enableCached;
    }

    public List<ShowCase> getShowCasesFromCache() {
        return showCasesFromCache;
    }
}

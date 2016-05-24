package com.frodo.github.business.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.github.bean.Repository;
import com.frodo.github.bean.User;
import com.frodo.github.common.Path;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by frodo on 2016/5/7.
 */
public class RepositoryModel extends AbstractModel {
    private AndroidFetchNetworkDataTask fetchRepositoryNetworkDataTask;

    public RepositoryModel(MainController controller) {
        super(controller);
    }

    @Override
    public void initBusiness() {
    }

    public Observable<Repository> loadRepositoryDetailWithReactor(final String slug) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(final Subscriber<? super String> subscriber) {
                Request request = new Request("GET", String.format(Path.Repos.REPOS_FULLNAME, slug));
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                fetchRepositoryNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, (Subscriber<String>) subscriber);
                getMainController().getBackgroundExecutor().execute(fetchRepositoryNetworkDataTask);
            }
        }).map(new Func1<String, Repository>() {
            @Override
            public Repository call(String s) {
                return JsonConverter.convert(s, new TypeReference<Repository>() {
                });
            }
        });
    }
}

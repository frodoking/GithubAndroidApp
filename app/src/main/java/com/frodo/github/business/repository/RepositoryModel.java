package com.frodo.github.business.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.github.bean.Repository;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.common.Path;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Subscriber;

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

    public void loadRepositoryDetailWithReactor(String slug, final Subscriber<? super Repository> subscriber) {
        Request request = new Request("GET", String.format("%s/%s", Path.Explore.V2_SHOWCASES, slug));
        final NetworkTransport networkTransport = getMainController().getNetworkTransport();
        networkTransport.setAPIUrl(Path.HOST_GITHUB);
        fetchRepositoryNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, new Subscriber<String>() {

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
                final String resultStr = s;
                try {
                    new JSONObject(resultStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                    return;
                }

                if (resultStr != null) {
                    Repository repository = JsonConverter.convert(resultStr, new TypeReference<Repository>() {
                    });
                    subscriber.onNext(repository);
                } else {
                    subscriber.onNext(null);
                }
            }
        });
        getMainController().getBackgroundExecutor().execute(fetchRepositoryNetworkDataTask);
    }
}

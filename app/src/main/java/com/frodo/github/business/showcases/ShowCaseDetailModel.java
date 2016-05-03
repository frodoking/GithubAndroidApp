package com.frodo.github.business.showcases;

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

import java.util.List;

import rx.Subscriber;

/**
 * Created by frodo on 2016/5/3.
 */
public class ShowCaseDetailModel extends AbstractModel {

    private AndroidFetchNetworkDataTask fetchShowCaseDetailNetworkDataTask;

    public ShowCaseDetailModel(MainController controller) {
        super(controller);
    }

    @Override
    public void initBusiness() {

    }

    public void loadShowCaseDetailWithReactor(String slug, final Subscriber<? super ShowCase> subscriber) {
        Request request = new Request("GET", String.format("%s/%s", Path.v2_showcases, slug));
        final NetworkTransport networkTransport = getMainController().getNetworkTransport();
        networkTransport.setAPIUrl("http://trending.codehub-app.com");
        fetchShowCaseDetailNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, new Subscriber<String>() {

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
                    new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                    return;
                }

                if (listString != null) {
                    ShowCase showCase = JsonConverter.convert(listString, new TypeReference<ShowCase>() {
                    });
                    subscriber.onNext(showCase);
                } else {
                    subscriber.onNext(null);
                }
            }
        });
        getMainController().getBackgroundExecutor().execute(fetchShowCaseDetailNetworkDataTask);
    }
}

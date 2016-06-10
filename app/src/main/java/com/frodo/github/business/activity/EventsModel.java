package com.frodo.github.business.activity;

import android.support.v4.util.Pair;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.net.Response;
import com.frodo.github.bean.dto.response.GithubEvent;
import com.frodo.github.common.Path;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by frodo on 16/6/10.
 */
public class EventsModel extends AbstractModel {
    public static final String TAG = EventsModel.class.getSimpleName();
    private AndroidFetchNetworkDataTask fetchReceivedEventsNetworkDataTask;

    public EventsModel(MainController controller) {
        super(controller);
    }

    @Override
    public void initBusiness() {
    }

    @Override
    public String name() {
        return TAG;
    }

    public Observable<List<GithubEvent>> loadReceivedEvents(final String username) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(Path.replace(Path.Activity.RECEIVED_EVENTS, new Pair<>("username", username)))
                        .build();
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                fetchReceivedEventsNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(fetchReceivedEventsNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<List<GithubEvent>>>() {
            @Override
            public Observable<List<GithubEvent>> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    List<GithubEvent> events = JsonConverter.convert(rb.string(), new TypeReference<List<GithubEvent>>() {
                    });
                    return Observable.just(events);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }
}

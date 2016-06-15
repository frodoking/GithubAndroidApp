package com.frodo.github.business.showcases;

import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.net.Response;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.common.Path;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by frodo on 2016/5/3.
 */
public class ShowCaseDetailModel extends AbstractModel {

    private AndroidFetchNetworkDataTask fetchShowCaseDetailNetworkDataTask;

    public ShowCaseDetailModel(MainController controller) {
        super(controller);
    }

    public Observable<ShowCase> loadShowCaseDetailWithReactor(final String slug) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(String.format("%s/%s", Path.Explore.SHOWCASES, slug))
                        .build();
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_CODEHUB);
                fetchShowCaseDetailNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(fetchShowCaseDetailNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<ShowCase>>() {
            @Override
            public Observable<ShowCase> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    ShowCase showCase = JsonConverter.convert(rb.string(), ShowCase.class);
                    return Observable.just(showCase);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }
}

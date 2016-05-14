package com.frodo.github.business.showcases;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.common.Path;

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

    @Override
    public void initBusiness() {
    }

    public Observable<ShowCase> loadShowCaseDetailWithReactor(final String slug) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                Request request = new Request("GET", String.format("%s/%s", Path.Explore.SHOWCASES, slug));
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_CODEHUB);
                fetchShowCaseDetailNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, (Subscriber<String>) subscriber);
                getMainController().getBackgroundExecutor().execute(fetchShowCaseDetailNetworkDataTask);
            }
        }).flatMap(new Func1<String, Observable<ShowCase>>() {
            @Override
            public Observable<ShowCase> call(final String s) {
                return Observable.create(new Observable.OnSubscribe<ShowCase>() {
                    @Override
                    public void call(Subscriber<? super ShowCase> subscriber) {
                        ShowCase showCase = JsonConverter.convert(s, new TypeReference<ShowCase>() {
                        });
                        subscriber.onNext(showCase);
                        subscriber.onCompleted();
                    }
                });
            }
        });
    }
}

package com.frodo.github.business.explore;

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

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by frodo on 2016/5/3.
 */
public class ShowCaseDetailModel extends AbstractModel
{

    private AndroidFetchNetworkDataTask fetchShowCaseDetailNetworkDataTask;

    public ShowCaseDetailModel(MainController controller)
    {
        super(controller);
    }

    public Observable<ShowCase> loadShowCaseDetailWithReactor(final String slug)
    {
        return Observable.create(new ObservableOnSubscribe<Response>()
        {
            @Override public void subscribe(ObservableEmitter<Response> emitter)
            {
                Request request = new Request.Builder().method("GET")
                        .relativeUrl(String.format("%s/%s", Path.Explore.SHOWCASES, slug)).build();
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_CODEHUB);
                fetchShowCaseDetailNetworkDataTask =
                        new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, emitter);
                getMainController().getBackgroundExecutor().execute(fetchShowCaseDetailNetworkDataTask);
            }
        }).flatMap(new Function<Response, ObservableSource<ShowCase>>()
        {
            @Override public ObservableSource<ShowCase> apply(Response response)
            {
                ResponseBody rb = (ResponseBody) response.getBody();
                try
                {
                    ShowCase showCase = JsonConverter.convert(rb.string(), ShowCase.class);
                    return Observable.just(showCase);
                }
                catch (IOException e)
                {
                    return Observable.error(e);
                }
            }
        });
    }
}

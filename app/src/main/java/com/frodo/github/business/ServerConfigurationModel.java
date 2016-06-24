package com.frodo.github.business;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.net.Response;
import com.frodo.github.bean.dto.response.GithubLanguage;
import com.frodo.github.common.Path;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 16/6/19.
 */
public class ServerConfigurationModel extends AbstractModel {

    public static final String TAG = ServerConfigurationModel.class.getSimpleName();

    public List<GithubLanguage> languages;

    public ServerConfigurationModel(MainController controller) {
        super(controller);
    }

    @Override
    public void initBusiness() {
        loadLanguagesWithReactor()
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<List<GithubLanguage>>() {
                    @Override
                    public void call(List<GithubLanguage> list) {
                        languages = list;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        languages = defaultLanguages();
                        throwable.printStackTrace();
                    }
                });
    }

    public List<GithubLanguage> getLanguages() {
        return languages == null || languages.isEmpty() ? defaultLanguages() : languages;
    }

    private Observable<List<GithubLanguage>> loadLanguagesWithReactor() {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(Path.Explore.LANGUAGES)
                        .build();
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_CODEHUB);
                AndroidFetchNetworkDataTask task = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                Future f = getMainController().getBackgroundExecutor().execute(task);
            }
        }).flatMap(new Func1<Response, Observable<List<GithubLanguage>>>() {
            @Override
            public Observable<List<GithubLanguage>> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    List<GithubLanguage> languages = JsonConverter.convert(rb.string(), new TypeReference<List<GithubLanguage>>() {
                    });

                    if (languages == null || languages.isEmpty()) {
                        return Observable.just(defaultLanguages());
                    } else {
                        return Observable.just(languages);
                    }
                } catch (IOException e) {
                    return Observable.just(languages);
                }
            }
        });
    }

    private List<GithubLanguage> defaultLanguages() {
        String languageJson = ResourceManager.getAsset("language.json");
        return JsonConverter.convert(languageJson, new TypeReference<List<GithubLanguage>>() {
        });
    }
}

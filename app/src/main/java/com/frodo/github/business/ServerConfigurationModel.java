package com.frodo.github.business;

import android.annotation.SuppressLint;
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

import com.google.gson.reflect.TypeToken;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by frodo on 16/6/19.
 */
public class ServerConfigurationModel extends AbstractModel
{

    public static final String TAG = ServerConfigurationModel.class.getSimpleName();

    public List<GithubLanguage> languages;

    public ServerConfigurationModel(MainController controller)
    {
        super(controller);
    }

    @SuppressLint ("CheckResult") @Override public void initBusiness()
    {
        loadLanguagesWithReactor().subscribeOn(Schedulers.io()).subscribe(new Consumer<List<GithubLanguage>>()
        {
            @Override public void accept(List<GithubLanguage> githubLanguages)
            {
                languages = githubLanguages;
            }
        }, new Consumer<Throwable>()
        {
            @Override public void accept(Throwable throwable)
            {
                languages = defaultLanguages();
                throwable.printStackTrace();
            }
        });
    }

    public List<GithubLanguage> getLanguages()
    {
        return languages == null || languages.isEmpty() ? defaultLanguages() : languages;
    }

    private Observable<List<GithubLanguage>> loadLanguagesWithReactor()
    {
        return Observable.create(new ObservableOnSubscribe<Response>()
        {
            @Override public void subscribe(ObservableEmitter<Response> emitter)
            {
                Request request = new Request.Builder().method("GET").relativeUrl(Path.Explore.LANGUAGES).build();
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_CODEHUB);
                AndroidFetchNetworkDataTask task =
                        new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, emitter);
                Future f = getMainController().getBackgroundExecutor().execute(task);
            }
        }).flatMap(new Function<Response, ObservableSource<List<GithubLanguage>>>()
        {
            @Override public ObservableSource<List<GithubLanguage>> apply(Response response)
            {
                ResponseBody rb = (ResponseBody) response.getBody();
                try
                {
                    List<GithubLanguage> languages =
                            JsonConverter.convert(rb.string(), new TypeToken<List<GithubLanguage>>()
                            {
                            }.getType());

                    if (languages == null || languages.isEmpty())
                    {
                        return Observable.just(defaultLanguages());
                    }
                    else
                    {
                        return Observable.just(languages);
                    }
                }
                catch (IOException e)
                {
                    return Observable.just(languages);
                }
            }
        });
    }

    private List<GithubLanguage> defaultLanguages()
    {
        String languageJson = ResourceManager.getAsset("language.json");
        return JsonConverter.convert(languageJson, new TypeToken<List<GithubLanguage>>()
        {
        }.getType());
    }
}

package com.frodo.github.business.user;

import android.content.Context;
import android.webkit.WebSettings;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.net.Response;
import com.frodo.app.framework.task.BackgroundCallTask;
import com.frodo.github.bean.dto.response.Repo;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.common.Path;
import com.frodo.github.datasource.WebApiProvider;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by frodo on 2016/5/30.
 */
public class UserModel extends AbstractModel {
    public static final String TAG = UserModel.class.getSimpleName();
    private AndroidFetchNetworkDataTask fetchUserNetworkDataTask;
    private BackgroundCallTask fetchUserFromWebTask;

    private WebApiProvider webApiProvider;

    public UserModel(MainController controller) {
        super(controller);
        final String userAgent = WebSettings.getDefaultUserAgent((Context) getMainController().getMicroContext());
        webApiProvider = new WebApiProvider(Path.HOST_GITHUB_WEB, userAgent);
    }

    @Override
    public void initBusiness() {
    }

    @Override
    public String name() {
        return TAG;
    }

    public Observable<User> loadUserWithReactor(final String username) {
        return Observable.combineLatest(loadUserWithReactor0(username), loadUserFromWebWithReactor(username), new Func2<User, User, User>() {
            @Override
            public User call(User user, User webUser) {
                user.starred = webUser.starred;
                user.popularRepositories = webUser.popularRepositories;
                user.contributeToRepositories = webUser.contributeToRepositories;
                return user;
            }
        });
    }

    public Observable<User> loadUserWithReactor0(final String username) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(String.format(Path.Users.USER, username))
                        .build();
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                fetchUserNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(fetchUserNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<User>>() {
            @Override
            public Observable<User> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    User user = JsonConverter.convert(rb.string(), User.class);
                    return Observable.just(user);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    private Observable<User> loadUserFromWebWithReactor(final String username) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @Override
            public void call(final Subscriber<? super User> subscriber) {
                fetchUserFromWebTask = new BackgroundCallTask<User>() {
                    @Override
                    public String key() {
                        return "WebUser";
                    }

                    @Override
                    public User runAsync() {
                        try {
                            return webApiProvider.getUser(username);
                        } catch (IOException e) {
                            subscriber.onError(e);
                            return null;
                        }
                    }

                    @Override
                    public void postExecute(User user) {
                        if (user != null) {
                            subscriber.onNext(user);
                        }
                        subscriber.onCompleted();
                    }
                };
                getMainController().getBackgroundExecutor().execute(fetchUserFromWebTask);
            }
        });
    }

    public Observable<List<User>> loadUsers() {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                Request request = new Request.Builder()
                        .method("GET")
                        .relativeUrl(String.format(Path.Users.USER_FOLLOWERS, "frodoking"))
                        .build();
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                fetchUserNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(fetchUserNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<List<User>>>() {
            @Override
            public Observable<List<User>> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    List<User> users = JsonConverter.convert(rb.string(), new TypeReference<List<User>>() {
                    });
                    return Observable.just(users);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }
}

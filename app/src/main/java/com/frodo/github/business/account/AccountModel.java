package com.frodo.github.business.account;

import android.util.Base64;

import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.cache.Cache;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.log.Logger;
import com.frodo.app.framework.net.Header;
import com.frodo.app.framework.net.NetworkInterceptor;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.net.Response;
import com.frodo.github.bean.dto.request.CreateAuthorization;
import com.frodo.github.bean.dto.response.GithubAuthorization;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.business.user.UserModel;
import com.frodo.github.common.AuthorizationConfig;
import com.frodo.github.common.GithubMediaTypes;
import com.frodo.github.common.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 * Created by frodo on 2016/5/5.
 */
public class AccountModel extends AbstractModel {

    public static final String TAG = AccountModel.class.getSimpleName();
    private UserModel userModel;

    private AndroidFetchNetworkDataTask createAuthorizationNetworkDataTask;
    private volatile String login;
    private volatile boolean isSignIn = false;

    public AccountModel(MainController controller) {
        super(controller);
    }

    @Override
    public void initBusiness() {
        if (userModel != null) return;

        Logger.fLog().tag("AccountModel").i("------- init -------");

        userModel = getMainController().getModelFactory()
                .getOrCreateIfAbsent(UserModel.TAG, UserModel.class, getMainController());

        if (getMainController().getCacheSystem().existCacheInInternal("login")) {
            final String username = getMainController().getCacheSystem().findCacheFromInternal("login", String.class);

            Logger.fLog().tag("Account").i("Login [ " + username + " ]");
            final String tokenKey = getBase64(username).trim();
            Logger.fLog().tag("Token").i("Key [ " + tokenKey + " ]");
            if (getMainController().getCacheSystem().existCacheInInternal(tokenKey)) {
                final String loginToken = getMainController().getCacheSystem().findCacheFromInternal(tokenKey, String.class);
                Logger.fLog().tag("Token").i("Cached Authorization [ " + loginToken + " ]");
                getMainController().getNetworkTransport().addInterceptor(new NetworkInterceptor.RequestInterceptor() {
                    @Override
                    public Void intercept(Request request) {
                        Logger.fLog().tag("Token").i("Interceptor Authorization(Cached) [ " + loginToken + " ]");
                        request.getHeaders().add(new Header("Authorization", "token " + loginToken));
                        return super.intercept(request);
                    }
                });
                this.login = username;
                this.isSignIn = true;
                return;
            }
        }

        this.login = null;
        this.isSignIn = false;
    }

    @Override
    public String name() {
        return TAG;
    }

    public Observable<User> loginUserWithReactor(final String username, final String password) {
        CreateAuthorization createAuthorization = new CreateAuthorization();
        createAuthorization.scopes = AuthorizationConfig.SCOPES;
        createAuthorization.client_id = AuthorizationConfig.CLIENT_ID;
        createAuthorization.client_secret = AuthorizationConfig.CLIENT_SECRET;
        createAuthorization.note = AuthorizationConfig.NOTE;
        return createAuthorization(username, password, createAuthorization)
                .flatMap(new Func1<GithubAuthorization, Observable<User>>() {
                    @Override
                    public Observable<User> call(final GithubAuthorization authorization) {
                        Logger.fLog().tag("Token").i("Create Authorization [ " + authorization.token + " ]");
                        final String tokenKey = getBase64(username).trim();
                        Logger.fLog().tag("Token").i("Key [ " + tokenKey + " ]");
                        getMainController().getCacheSystem().put(tokenKey, authorization.token, Cache.Type.INTERNAL);
                        getMainController().getNetworkTransport().addInterceptor(new NetworkInterceptor.RequestInterceptor() {
                            @Override
                            public Void intercept(Request request) {
                                Logger.fLog().tag("Token").i("Interceptor Authorization [ " + authorization.token + " ]");
                                request.getHeaders().add(new Header("Authorization", "token " + authorization.token));
                                return super.intercept(request);
                            }
                        });

                        return userModel.loadUserWithReactor0(username);
                    }
                }).doOnNext(new Action1<User>() {
                    @Override
                    public void call(User user) {
                        if (user != null) {
                            getMainController().getCacheSystem().put("login", user.login, Cache.Type.INTERNAL);
                            login = user.login;
                            isSignIn = true;
                        } else {
                            login = null;
                            isSignIn = false;
                        }
                    }
                });
    }

    public Observable<Void> logoutUserWithReactor() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                try {
                    getMainController().getCacheSystem().evict(login);
                    login = null;
                    isSignIn = false;
                    subscriber.onNext(null);
                } catch (Exception e) {
                    subscriber.onError(e);
                }
                subscriber.onCompleted();
            }
        });
    }

    /**
     * Non-Web Application Flow
     * Use Basic Authentication to create an OAuth2 token
     *
     * @param username
     * @param password
     * @param createAuthorization
     * @return
     */
    private Observable<GithubAuthorization> createAuthorization(final String username, final String password, final CreateAuthorization createAuthorization) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                List<Header> headerList = new ArrayList<>();
                RequestBody requestBody = RequestBody.create(MediaType.parse(GithubMediaTypes.BasicJson), JsonConverter.toJson(createAuthorization));
                String userCredentials = username + ":" + password;
                String basicAuth = "Basic " + getBase64(userCredentials);
                headerList.add(new Header("Authorization", basicAuth.trim()));
                headerList.add(new Header("Accept", GithubMediaTypes.BasicJson));

                Request request = new Request.Builder<RequestBody>()
                        .method("POST")
                        .relativeUrl(Path.Authentication.AUTHORIZATIONS)
                        .headers(headerList)
                        .body(requestBody)
                        .build();
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                createAuthorizationNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(createAuthorizationNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<GithubAuthorization>>() {
            @Override
            public Observable<GithubAuthorization> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    GithubAuthorization githubAuthorization = JsonConverter.convert(rb.string(), GithubAuthorization.class);
                    return Observable.just(githubAuthorization);
                } catch (IOException e) {
                    return Observable.error(e);
                }
            }
        });
    }

    public boolean isSignIn() {
        return isSignIn;
    }

    public String getSignInUser() {
        return login;
    }

    private static String getBase64(String string) {
        return new String(Base64.encode(string.getBytes(), Base64.DEFAULT));
    }
}

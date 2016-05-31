package com.frodo.github.business.account;

import android.util.Base64;

import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.cache.Cache;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.Header;
import com.frodo.app.framework.net.NetworkInterceptor;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.net.Response;
import com.frodo.github.bean.dto.request.CreateAuthorization;
import com.frodo.github.bean.dto.request.RequestTokenDTO;
import com.frodo.github.bean.dto.response.GithubAuthorization;
import com.frodo.github.bean.dto.response.Token;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.business.user.UserModel;
import com.frodo.github.common.Path;
import com.frodo.github.common.Scope;

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
    private AndroidFetchNetworkDataTask requestTokenNetworkDataTask;
    private volatile String login;
    private volatile boolean isSignIn;

    public AccountModel(MainController controller) {
        super(controller);
        if (userModel == null) {
            initBusiness();
        }
    }

    @Override
    public void initBusiness() {
        userModel = getMainController().getModelFactory()
                .getOrCreateIfAbsent(UserModel.TAG, UserModel.class, getMainController());

        if (getMainController().getCacheSystem().existCacheInInternal("login")) {
            this.login = getMainController().getCacheSystem().findCacheFromInternal("login", String.class);
            this.isSignIn = true;

            final Object loginToken = getMainController().getCacheSystem().findCacheFromInternal(login, String.class);
            if (loginToken != null) {
                getMainController().getNetworkTransport().addInterceptor(new NetworkInterceptor.RequestInterceptor() {
                    @Override
                    public Void intercept(Request request) {
                        request.getHeaders().add(new Header("Authorization", "token " + loginToken));
                        return super.intercept(request);
                    }
                });
            }
        } else {
            this.login = null;
            this.isSignIn = false;
        }
    }

    @Override
    public String name() {
        return TAG;
    }

    public Observable<User> loginUserWithReactor(final String username, final String password) {
        CreateAuthorization createAuthorization = new CreateAuthorization();
        createAuthorization.scopes = new String[]{
                Scope.ADMIN_ORG, Scope.ADMIN_ORG_HOOK, Scope.ADMIN_PUBLIC_KEY, Scope.ADMIN_REPO_HOOK,
                Scope.DELETE_REPO,
                Scope.GIST,
                Scope.NOTIFICATIONS,
                Scope.REPO,
                Scope.USER,
        };
        createAuthorization.note = "GithubAndroidClient";
        return createAuthorization(username, password, createAuthorization).doOnNext(new Action1<GithubAuthorization>() {
            @Override
            public void call(final GithubAuthorization githubAuthorization) {
                getMainController().getNetworkTransport().addInterceptor(new NetworkInterceptor.RequestInterceptor() {
                    @Override
                    public Void intercept(Request request) {
                        getMainController().getCacheSystem().put(username, githubAuthorization.token, Cache.Type.INTERNAL);
                        request.getHeaders().add(new Header("Authorization", "token " + githubAuthorization.token));
                        return super.intercept(request);
                    }
                });
            }
        }).flatMap(new Func1<GithubAuthorization, Observable<User>>() {
            @Override
            public Observable<User> call(GithubAuthorization githubAuthorization) {
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
    public Observable<GithubAuthorization> createAuthorization(final String username, final String password, final CreateAuthorization createAuthorization) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                List<Header> headerList = new ArrayList<>();
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/vnd.github.v3+json"), JsonConverter.toJson(createAuthorization));
                String userCredentials = username + ":" + password;
                String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes(), Base64.DEFAULT));
                headerList.add(new Header("Authorization", basicAuth.trim()));
                headerList.add(new Header("Accept", "application/vnd.github.v3.json"));

                Request request = new Request.Builder<RequestBody>()
                        .method("POST")
                        .relativeUrl(Path.Login.AUTHORIZATIONS)
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

    /**
     * Web Application Flow
     *
     * @param code
     * @param clientId
     * @param clientSecret
     * @param redirectUri
     * @return
     */
    public Observable<Token> requestToken(final String code, final String clientId, final String clientSecret, final String redirectUri) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                RequestTokenDTO tokenDTO = new RequestTokenDTO();
                tokenDTO.client_id = clientId;
                tokenDTO.client_secret = clientSecret;
                tokenDTO.redirect_uri = redirectUri;
                tokenDTO.code = code;

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/vnd.github.v3+json"), JsonConverter.toJson(tokenDTO));
                List<Header> headerList = new ArrayList<>();
                headerList.add(new Header("Accept", "application/json"));

                Request request = new Request.Builder<RequestBody>()
                        .method("POST")
                        .relativeUrl(Path.Login.OAUTH_ACCESS_TOKEN)
                        .headers(headerList)
                        .body(requestBody)
                        .build();

                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                requestTokenNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, subscriber);
                getMainController().getBackgroundExecutor().execute(requestTokenNetworkDataTask);
            }
        }).flatMap(new Func1<Response, Observable<Token>>() {
            @Override
            public Observable<Token> call(Response response) {
                ResponseBody rb = (ResponseBody) response.getBody();
                try {
                    Token token = JsonConverter.convert(rb.string(), Token.class);
                    return Observable.just(token);
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
}

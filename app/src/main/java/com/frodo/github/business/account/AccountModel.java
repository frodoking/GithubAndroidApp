package com.frodo.github.business.account;

import android.content.Context;
import android.util.Base64;

import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.Header;
import com.frodo.app.framework.net.NetworkInterceptor;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.github.bean.dto.request.CreateAuthorization;
import com.frodo.github.bean.dto.request.RequestTokenDTO;
import com.frodo.github.bean.dto.response.GithubAuthorization;
import com.frodo.github.bean.dto.response.Token;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.common.Path;
import com.frodo.github.common.Scope;
import com.frodo.github.common.SharePreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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
    private volatile User signInUser;
    private volatile boolean isSignIn;

    public AccountModel(MainController controller) {
        super(controller);
    }

    @Override
    public void initBusiness() {
        userModel = getMainController().getModelFactory()
                .getOrCreateIfAbsent(UserModel.TAG, UserModel.class, getMainController());

        Object userState = SharePreferenceHelper.getPreferences("user", (Context) getMainController().getMicroContext());
        if (userState != null && userState instanceof User) {
            signInUser = (User) userState;
            isSignIn = true;

            final Object loginToken = SharePreferenceHelper.getPreferences(signInUser.login, (Context) getMainController().getMicroContext());
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
            signInUser = null;
            isSignIn = false;
        }
    }

    @Override
    public String name() {
        return TAG;
    }

    public Observable<User> loginUserWithReactor(final String username, final String password) {
        CreateAuthorization createAuthorization = new CreateAuthorization();
        createAuthorization.scopes = new String[]{Scope.REPO};
        createAuthorization.note = "GithubAndroidClient";
        return createAuthorization(username, password, createAuthorization).doOnNext(new Action1<GithubAuthorization>() {
            @Override
            public void call(final GithubAuthorization githubAuthorization) {
                getMainController().getNetworkTransport().addInterceptor(new NetworkInterceptor.RequestInterceptor() {
                    @Override
                    public Void intercept(Request request) {
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
                    signInUser = user;
                    isSignIn = true;
                } else {
                    signInUser = null;
                    isSignIn = false;
                }
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
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                List<Header> headerList = new ArrayList<>();
                RequestBody requestBody = RequestBody.create(MediaType.parse("application/vnd.github.v3+json"), JsonConverter.toJson(createAuthorization));
                String userCredentials = username + ":" + password;
                String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes(), Base64.DEFAULT));
                headerList.add(new Header("Authorization", basicAuth.trim()));
                headerList.add(new Header("Accept", "application/vnd.github.v3.json"));

                Request<RequestBody> request = new Request<>("POST", Path.Login.AUTHORIZATIONS, null, headerList, requestBody);
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                createAuthorizationNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, (Subscriber<String>) subscriber);
                getMainController().getBackgroundExecutor().execute(createAuthorizationNetworkDataTask);
            }
        }).map(new Func1<String, GithubAuthorization>() {
            @Override
            public GithubAuthorization call(String s) {
                return JsonConverter.convert(s, GithubAuthorization.class);
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
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                RequestTokenDTO tokenDTO = new RequestTokenDTO();
                tokenDTO.client_id = clientId;
                tokenDTO.client_secret = clientSecret;
                tokenDTO.redirect_uri = redirectUri;
                tokenDTO.code = code;

                RequestBody requestBody = RequestBody.create(MediaType.parse("application/vnd.github.v3+json"), JsonConverter.toJson(tokenDTO));
                List<Header> headerList = new ArrayList<>();
                headerList.add(new Header("Accept", "application/json"));

                Request<RequestBody> request = new Request<>("POST", Path.Login.OAUTH_ACCESS_TOKEN, null, headerList, requestBody);
                final NetworkTransport networkTransport = getMainController().getNetworkTransport();
                networkTransport.setAPIUrl(Path.HOST_GITHUB);
                requestTokenNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, (Subscriber<String>) subscriber);
                getMainController().getBackgroundExecutor().execute(requestTokenNetworkDataTask);
            }
        }).map(new Func1<String, Token>() {
            @Override
            public Token call(String s) {
                return JsonConverter.convert(s, Token.class);
            }
        });
    }

    public boolean isSignIn() {
        return isSignIn;
    }

    public User getSignInUser() {
        return signInUser;
    }
}

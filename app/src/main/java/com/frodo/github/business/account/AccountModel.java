package com.frodo.github.business.account;

import android.content.Context;
import android.util.Base64;
import android.webkit.WebSettings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.Header;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.task.BackgroundCallTask;
import com.frodo.github.bean.Authorization;
import com.frodo.github.bean.CreateAuthorization;
import com.frodo.github.bean.User;
import com.frodo.github.common.Path;
import com.frodo.github.common.Scope;
import com.frodo.github.datasource.WebApiProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import rx.Subscriber;

/**
 * Created by frodo on 2016/5/5.
 */
public class AccountModel extends AbstractModel {
    private AndroidFetchNetworkDataTask createAuthorizationNetworkDataTask;
    private AndroidFetchNetworkDataTask fetchUserNetworkDataTask;
    private BackgroundCallTask fetchUserFromWebTask;

    private WebApiProvider webApiProvider;
    private final CountDownLatch countDownLatch = new CountDownLatch(2);
    private User user;

    public AccountModel(MainController controller) {
        super(controller);
        final String userAgent = WebSettings.getDefaultUserAgent((Context) getMainController().getMicroContext());
        webApiProvider = new WebApiProvider(Path.HOST_GITHUB_WEB, userAgent);
    }

    @Override
    public void initBusiness() {
    }

    @Override
    public String name() {
        return getClass().getCanonicalName();
    }

    public User login(String username, String password, Subscriber<? super User> subscriber) {
        CreateAuthorization createAuthorization = new CreateAuthorization();
        createAuthorization.scopes = new String[]{Scope.REPO};
        createAuthorization.note = "GithubAndroidClient";
        createAuthorization(username, password, createAuthorization);
        return null;
    }

    public void createAuthorization(String username, String password, CreateAuthorization createAuthorization) {
        List<Header> headerList = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = objectMapper.writeValueAsString(createAuthorization);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/vnd.github.v3+json"), jsonString);
        String userCredentials = username + ":" + password;
        String basicAuth = "Basic " + new String(Base64.encode(userCredentials.getBytes(), Base64.DEFAULT));
        headerList.add(new Header("Authorization", basicAuth.trim()));
        headerList.add(new Header("Accept", "application/vnd.github.v3.json"));

        Request<RequestBody> request = new Request<>("POST", Path.Login.AUTHORIZATIONS, null, headerList, requestBody);

        final NetworkTransport networkTransport = getMainController().getNetworkTransport();
        networkTransport.setAPIUrl(Path.HOST_GITHUB);
        createAuthorizationNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, new Subscriber<String>() {

            @Override
            public void onStart() {
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(String s) {
                final String listString = s;
                try {
                    new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                if (listString != null) {
                    Authorization authorization = JsonConverter.convert(listString, Authorization.class);
                }
            }
        });
        getMainController().getBackgroundExecutor().execute(createAuthorizationNetworkDataTask);
    }

    public void loadUserWithReactor(final String username, final Subscriber<? super User> subscriber) {
        Request request = new Request("GET", String.format(Path.Users.USER, username));
        final NetworkTransport networkTransport = getMainController().getNetworkTransport();
        networkTransport.setAPIUrl(Path.HOST_GITHUB);
        fetchUserNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, new Subscriber<String>() {

            @Override
            public void onStart() {
                super.onStart();
                subscriber.onStart();
            }

            @Override
            public void onCompleted() {
                if (countDownLatch.getCount() == 0) {
                    subscriber.onCompleted();
                }
            }

            @Override
            public void onError(Throwable e) {
                countDownLatch.countDown();
                subscriber.onError(e);
            }

            @Override
            public void onNext(String s) {
                final String resultStr = s;
                try {
                    new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                    return;
                }

                countDownLatch.countDown();
                if (resultStr != null) {
                    User user = JsonConverter.convert(resultStr, new TypeReference<User>() {
                    });
                    checkUser(user, subscriber);
                } else {
                    checkUser(null, subscriber);
                }
            }
        });
        getMainController().getBackgroundExecutor().execute(fetchUserNetworkDataTask);

        fetchUserFromWebTask = new BackgroundCallTask<User>() {
            @Override
            public String key() {
                return "WebUser";
            }

            @Override
            public User runAsync() {
                return webApiProvider.getUser(username);
            }

            @Override
            public void postExecute(User user) {
                countDownLatch.countDown();
                if (user != null) {
                    checkUser(user, subscriber);
                }else{
                    checkUser(null, subscriber);
                }
            }
        };


        getMainController().getBackgroundExecutor().execute(fetchUserFromWebTask);
    }

    private void checkUser(User user, Subscriber<? super User> subscriber) {
        if (this.user == null) {
            this.user = user;
        } else {
            if (this.user.name != null) {
                this.user.starred = user.starred;
                this.user.popularRepositories = user.popularRepositories;
                this.user.contributeToRepositories = user.contributeToRepositories;
            }
        }

        if (countDownLatch.getCount() == 0) {
            subscriber.onNext(this.user);
        }
    }
}

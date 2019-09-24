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
import com.frodo.github.bean.dto.response.GithubAuthorization;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.business.user.UserModel;
import com.frodo.github.common.AuthorizationConfig;
import com.frodo.github.common.GitHubMediaTypes;
import com.frodo.github.common.Path;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

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

	private static String getBase64(String string) {
		return new String(Base64.encode(string.getBytes(), Base64.DEFAULT));
	}

	@Override
	public void initBusiness() {
		if (userModel != null) return;

		userModel = getMainController().getModelFactory()
				.getOrCreateIfAbsent(UserModel.TAG, UserModel.class, getMainController());

		if (getMainController().getCacheSystem().existCacheInInternal("login")) {
			final String username = getMainController().getCacheSystem().findCacheFromInternal("login", String.class);
			final String tokenKey = getBase64(username).trim();
			if (getMainController().getCacheSystem().existCacheInInternal(tokenKey)) {
				final String loginToken = getMainController().getCacheSystem().findCacheFromInternal(tokenKey, String.class);
				getMainController().getNetworkTransport().addInterceptor(new NetworkInterceptor.RequestInterceptor() {
					@Override
					public Void intercept(Request request) {
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

	public Observable<User> loginUserWithReactor(final String username, final String password) {
		CreateAuthorization createAuthorization = new CreateAuthorization();
		createAuthorization.scopes = AuthorizationConfig.SCOPES;
		createAuthorization.client_id = AuthorizationConfig.CLIENT_ID;
		createAuthorization.client_secret = AuthorizationConfig.CLIENT_SECRET;
		createAuthorization.note = AuthorizationConfig.NOTE;
		return createAuthorization(username, password, createAuthorization)
				.flatMap(new Function<GithubAuthorization, ObservableSource<User>>()
				{
					@Override public ObservableSource<User> apply(final GithubAuthorization authorization)
					{
						final String tokenKey = getBase64(username).trim();
						getMainController().getCacheSystem().put(tokenKey, authorization.token, Cache.Type.INTERNAL);
						getMainController().getNetworkTransport().addInterceptor(new NetworkInterceptor.RequestInterceptor() {
							@Override
							public Void intercept(Request request) {
								request.getHeaders().add(new Header("Authorization", "token " + authorization.token));
								return super.intercept(request);
							}
						});

						return userModel.loadUserWithReactor0(username);
					}
				}).doAfterNext(new Consumer<User>()
				{
					@Override public void accept(User user)
					{
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
		return Observable.create(new ObservableOnSubscribe<Void>() {

			@Override public void subscribe(ObservableEmitter<Void> emitter)
			{
				try {
					getMainController().getCacheSystem().evict(getBase64(login).trim(), Cache.Type.INTERNAL);
					login = null;
					isSignIn = false;
					emitter.onNext(null);
				} catch (Exception e) {
					emitter.onError(e);
				}
				emitter.onComplete();
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
		return Observable.create(new ObservableOnSubscribe<Response>() {

			@Override public void subscribe(ObservableEmitter<Response> emitter)
			{
				List<Header> headerList = new ArrayList<>();
				RequestBody requestBody = RequestBody.create(MediaType.parse(GitHubMediaTypes.BasicJson), JsonConverter.toJson(createAuthorization));
				String userCredentials = username + ":" + password;
				String basicAuth = "Basic " + getBase64(userCredentials);
				headerList.add(new Header("Authorization", basicAuth.trim()));
				headerList.add(new Header("Accept", GitHubMediaTypes.BasicJson));

				Request request = new Request.Builder<RequestBody>()
						.method("POST")
						.relativeUrl(Path.Authentication.AUTHORIZATIONS)
						.headers(headerList)
						.body(requestBody)
						.build();
				final NetworkTransport networkTransport = getMainController().getNetworkTransport();
				networkTransport.setAPIUrl(Path.HOST_GITHUB);
				createAuthorizationNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, emitter);
				getMainController().getBackgroundExecutor().execute(createAuthorizationNetworkDataTask);
			}
		}).flatMap(new Function<Response, ObservableSource<GithubAuthorization>>()
		{

			@Override
			public ObservableSource<GithubAuthorization> apply(Response response) {
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
}

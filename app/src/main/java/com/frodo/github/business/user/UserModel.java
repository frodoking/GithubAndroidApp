package com.frodo.github.business.user;

import com.fasterxml.jackson.core.type.TypeReference;
import android.content.Context;
import android.support.v4.util.Pair;
import android.webkit.WebSettings;

import com.frodo.app.android.core.task.AndroidFetchNetworkDataTask;
import com.frodo.app.android.core.toolbox.JsonConverter;
import com.frodo.app.framework.controller.AbstractModel;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.net.Header;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.net.Response;
import com.frodo.app.framework.task.BackgroundCallTask;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.bean.dto.response.search.UsersSearch;
import com.frodo.github.common.Path;
import com.frodo.github.datasource.WebApiProvider;

import java.io.IOException;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;

/**
 * Created by frodo on 2016/5/30.
 */
public class UserModel extends AbstractModel {
	public static final String TAG = UserModel.class.getSimpleName();
	private AndroidFetchNetworkDataTask fetchUserNetworkDataTask;
	private AndroidFetchNetworkDataTask searchUsersNetworkDataTask;
	private BackgroundCallTask fetchUserFromWebTask;

	private WebApiProvider webApiProvider;

	public UserModel(MainController controller) {
		super(controller);
		final String userAgent = WebSettings.getDefaultUserAgent((Context) getMainController().getMicroContext().getContext());
		webApiProvider = new WebApiProvider(Path.HOST_GITHUB_WEB, userAgent);
	}

	@Override
	public void initBusiness() {
	}

	public Observable<User> loadUserWithReactor(final String username) {
		return Observable.combineLatest(loadUserWithReactor0(username), loadUserFromWebWithReactor(username), new BiFunction<User, User, User>() {
			@Override public User apply(User user, User webUser)
			{
				user.starred = webUser.starred;
				user.popularRepositories = webUser.popularRepositories;
				user.contributeToRepositories = webUser.contributeToRepositories;
				return user;
			}
		});
	}

	public Observable<User> loadUserWithReactor0(final String username) {
		return Observable.create(new ObservableOnSubscribe<Response>() {
			@Override public void subscribe(ObservableEmitter<Response> emitter)
			{
				Request request = new Request.Builder()
						.method("GET")
						.relativeUrl(Path.replace(Path.Users.USER, new Pair<>("username", username)))
						.build();
				final NetworkTransport networkTransport = getMainController().getNetworkTransport();
				networkTransport.setAPIUrl(Path.HOST_GITHUB);
				fetchUserNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, emitter);
				getMainController().getBackgroundExecutor().execute(fetchUserNetworkDataTask);
			}
		}).flatMap(new Function<Response, ObservableSource<User>>() {
			@Override public ObservableSource<User> apply(Response response)
			{
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
		return Observable.create(new ObservableOnSubscribe<User>() {
			@Override public void subscribe(final ObservableEmitter<User> emitter)
			{
				fetchUserFromWebTask = new BackgroundCallTask<User>() {
					@Override
					public String key() {
						return "WebUser";
					}

					@Override
					public User runAsync() {
						try {
							return webApiProvider.getUser(username);
						} catch (Exception e) {
							emitter.onError(e);
							return null;
						}
					}

					@Override
					public void postExecute(User user) {
						if (user != null) {
							emitter.onNext(user);
						}
						emitter.onComplete();
					}
				};
				getMainController().getBackgroundExecutor().execute(fetchUserFromWebTask);
			}
		});
	}

	public Observable<List<User>> loadRepoStargazers(String ownerName, String repoName) {
		return loadUsers(Path.replace(Path.Repositories.REPOS_STARGAZERS, new Pair<>("owner", ownerName), new Pair<>("repo", repoName)));
	}

	public Observable<List<User>> loadRepoWatchers(String ownerName, String repoName) {
		return loadUsers(Path.replace(Path.Repositories.REPOS_WATCHERS, new Pair<>("owner", ownerName), new Pair<>("repo", repoName)));
	}

	public Observable<List<User>> loadUserFollowers(String username) {
		return loadUsers(Path.replace(Path.Users.USER_FOLLOWERS, new Pair<>("username", username)));
	}

	public Observable<List<User>> loadUserFollowing(String username) {
		return loadUsers(Path.replace(Path.Users.USER_FOLLOWING, new Pair<>("username", username)));
	}

	private Observable<List<User>> loadUsers(final String path) {
		return Observable.create(new ObservableOnSubscribe<Response>() {
			@Override public void subscribe(ObservableEmitter<Response> emitter)
			{
				Request request = new Request.Builder()
						.method("GET")
						.relativeUrl(path)
						.build();
				final NetworkTransport networkTransport = getMainController().getNetworkTransport();
				networkTransport.setAPIUrl(Path.HOST_GITHUB);
				fetchUserNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, emitter);
				getMainController().getBackgroundExecutor().execute(fetchUserNetworkDataTask);
			}
		}).flatMap(new Function<Response, ObservableSource<List<User>>>() {
			@Override public ObservableSource<List<User>> apply(Response response)
			{
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

	public Observable<Boolean> followingUser(final String username) {
		return Observable.create(new ObservableOnSubscribe<Response>() {
			@Override public void subscribe(ObservableEmitter<Response> emitter)
			{
				Request request = new Request.Builder()
						.method("GET")
						.relativeUrl(Path.replace(Path.Users.USER_FOLLOW, new Pair<>("username", username)))
						.build();
				request.getHeaders().add(new Header("Content-Length", "0"));
				final NetworkTransport networkTransport = getMainController().getNetworkTransport();
				networkTransport.setAPIUrl(Path.HOST_GITHUB);
				fetchUserNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, emitter);
				getMainController().getBackgroundExecutor().execute(fetchUserNetworkDataTask);
			}
		}).map(new Function<Response, Boolean>() {
			@Override public Boolean apply(Response response)
			{
				return response.getStatus() == 204;
			}
		});
	}

	public Observable<UsersSearch> searchUsers(final String q, final String sort, final String order) {
		return searchUsers(q, sort, order, -1, -1);
	}

	public Observable<UsersSearch> searchUsers(final String q, final String sort, final String order, final int page, final int perPage) {
		return Observable.create(new ObservableOnSubscribe<Response>() {
			@Override public void subscribe(ObservableEmitter<Response> emitter)
			{
				Request request = new Request.Builder()
						.method("GET")
						.relativeUrl(Path.Search.USERS)
						.build();
				Path.warpRequestMethodAddQueryParam(request, "q", TextUtils.isEmpty(q) ? "" : q);

				if (!TextUtils.isEmpty(sort))
					Path.warpRequestMethodAddQueryParam(request, "sort", sort);
				if (!TextUtils.isEmpty(order))
					Path.warpRequestMethodAddQueryParam(request, "order", order);

				if (page != -1)
					Path.warpRequestMethodAddQueryParam(request, "page", String.valueOf(page));

				if (perPage != -1)
					Path.warpRequestMethodAddQueryParam(request, "per_page", String.valueOf(perPage));

				final NetworkTransport networkTransport = getMainController().getNetworkTransport();
				networkTransport.setAPIUrl(Path.HOST_GITHUB);
				searchUsersNetworkDataTask = new AndroidFetchNetworkDataTask(getMainController().getNetworkTransport(), request, emitter);
				getMainController().getBackgroundExecutor().execute(searchUsersNetworkDataTask);
			}
		}).flatMap(new Function<Response, ObservableSource<UsersSearch>>() {
			@Override public ObservableSource<UsersSearch> apply(Response response)
			{
				ResponseBody rb = (ResponseBody) response.getBody();
				try {
					UsersSearch usersSearch = JsonConverter.convert(rb.string(), new TypeReference<UsersSearch>() {
					});
					return Observable.just(usersSearch);
				} catch (IOException e) {
					return Observable.error(e);
				}
			}
		});
	}

	Observable<Void> doFollow(String username) {
		return Observable.create(new ObservableOnSubscribe<Response>() {
			@Override public void subscribe(ObservableEmitter<Response> emitter)
			{

			}
		}).map(new Function<Response, Void>() {
			@Override public Void apply(Response response) throws Exception
			{
				return null;
			}
		});
	}
}

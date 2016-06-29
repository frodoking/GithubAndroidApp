package com.frodo.github;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.frodo.app.android.ApplicationDelegation;
import com.frodo.app.android.MicroContextLoader;
import com.frodo.app.android.core.config.AndroidConfig;
import com.frodo.app.android.core.log.AndroidLogCollectorSystem;
import com.frodo.app.android.core.network.AndroidNetworkSystem;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.app.framework.config.Configuration;
import com.frodo.app.framework.config.Environment;
import com.frodo.app.framework.controller.IController;
import com.frodo.app.framework.controller.MainController;
import com.frodo.app.framework.log.LogCollector;
import com.frodo.app.framework.log.Logger;
import com.frodo.app.framework.net.Header;
import com.frodo.app.framework.net.NetworkInterceptor;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.net.Response;
import com.frodo.app.framework.theme.Theme;
import com.frodo.github.business.ServerConfigurationModel;
import com.frodo.github.business.account.AccountModel;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.util.List;

/**
 * Created by frodo on 2016/4/28.
 */
public class GitHubApplication extends Application implements ApplicationDelegation {

	private MicroContextLoader contextLoader;

	@Override
	public void onCreate() {
		super.onCreate();
		beforeLoad();
		contextLoader = loadMicroContextLoader();
		afterLoad();
		Log.i("Application_Life_Cycle", "onCreate");
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.i("Application_Life_Cycle", "onLowMemory");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.i("Application_Life_Cycle", "onTerminate");
	}

	@Override
	public void onTrimMemory(int level) {
		super.onTrimMemory(level);
		Log.i("Application_Life_Cycle", "onTrimMemory -- > level:" + level);
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		MultiDex.install(this);
	}

	@Override
	public void beforeLoad() {
	}

	@Override
	public MicroContextLoader loadMicroContextLoader() {
		return new MicroContextLoader(this) {
			@Override
			public Theme loadTheme() {
				return new Theme() {
					@Override
					public int themeColor() {
						return ResourceManager.getColor(R.color.colorPrimaryDark);
					}
				};
			}

			@Override
			public Configuration loadConfiguration() {
				final Environment environment = new Environment(0, "githubV3", "api.github.com", "", BuildConfig.DEBUG);
				return new AndroidConfig(getMainController(), environment);
			}

			@Override
			public LogCollector loadLogCollector() {
				return new AndroidLogCollectorSystem(getMainController(), BuildConfig.DEBUG ? LogCollector.VERBOSE : LogCollector.ASSERT) {
					@Override
					public void uploadLeakBlocking(File file, String leakInfo) {
					}
				};
			}

			@Override
			public NetworkTransport loadNetworkTransport() {
				return new SimpleAndroidNetworkSystem(getMainController());
			}

			@Override
			public void loadServerConfiguration() {
				getMainController()
						.getModelFactory()
						.getOrCreateIfAbsent(ServerConfigurationModel.TAG, ServerConfigurationModel.class, getMainController())
						.initBusiness();
			}
		};
	}

	@Override
	public void afterLoad() {
		MobileAds.initialize(this, "ca-app-pub-5257007452683157~9157734222");

		getMainController()
				.getModelFactory()
				.getOrCreateIfAbsent(AccountModel.TAG, AccountModel.class, getMainController())
				.initBusiness();
	}

	@Override
	public MainController getMainController() {
		return contextLoader.getMainController();
	}

	private static class SimpleAndroidNetworkSystem extends AndroidNetworkSystem {

		private SimpleAndroidNetworkSystem(IController controller) {
			super(controller);
			setAPIUrl(controller.getConfig().getCurrentEnvironment().getUrl());
			addInterceptor(new NetworkInterceptor.RequestInterceptor() {
				@Override
				public Void intercept(Request request) {
					return super.intercept(request);
				}
			});
			addInterceptor(new NetworkInterceptor.ResponseSuccessInterceptor() {
				@Override
				public Void intercept(Response response) {
					List<Header> headers = response.getHeaders();
					if (headers != null && !headers.isEmpty()) {
						StringBuilder sb = new StringBuilder();
						sb.append("\n");
						for (Header header : headers) {
							sb.append(header.getName()).append(" : ").append(header.getValue()).append("\n");
						}
						Logger.fLog().tag("Response").i(sb.toString());
					}

					return super.intercept(response);
				}
			});
		}
	}
}

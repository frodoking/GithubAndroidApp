package com.frodo.github;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.frodo.app.android.MicroApplication;
import com.frodo.app.android.core.config.AndroidConfig;
import com.frodo.app.android.core.exception.AndroidCrashHandler;
import com.frodo.app.android.core.log.AndroidLogCollectorSystem;
import com.frodo.app.android.core.network.AndroidNetworkSystem;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.app.framework.config.Configuration;
import com.frodo.app.framework.config.Environment;
import com.frodo.app.framework.controller.IController;
import com.frodo.app.framework.exception.ExceptionHandler;
import com.frodo.app.framework.log.LogCollector;
import com.frodo.app.framework.log.Logger;
import com.frodo.app.framework.net.Header;
import com.frodo.app.framework.net.NetworkInterceptor;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.net.Response;
import com.frodo.app.framework.scene.DefaultScene;
import com.frodo.app.framework.scene.Scene;
import com.frodo.app.framework.theme.Theme;
import com.frodo.github.business.account.AccountModel;

import java.io.File;
import java.util.List;

/**
 * Created by frodo on 2016/4/28.
 */
public class GithubApplicatioin extends MicroApplication {

    @Override
    public void init() {
        super.init();
        Fresco.initialize(this);
        getMainController().getLogCollector().enableCollect(true);

        getMainController()
                .getModelFactory()
                .getOrCreateIfAbsent(AccountModel.TAG, AccountModel.class, getMainController())
                .initBusiness();
    }

    @Override
    public LogCollector loadLogCollector() {
        return new AndroidLogCollectorSystem(getMainController()) {
            @Override
            public void uploadLeakBlocking(File file, String leakInfo) {
            }
        };
    }

    @Override
    public Configuration loadConfiguration() {
        final Environment environment = new Environment(0, "githubV3", "api.github.com", "", true);
        return new AndroidConfig(getMainController(), environment);
    }

    @Override
    public Scene loadScene() {
        return new DefaultScene();
    }

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
    public NetworkTransport loadNetworkTransport() {
        return new SimpleAndroidNetworkSystem(getMainController());
    }

    @Override
    public ExceptionHandler loadExceptionHandler() {
        return new AndroidCrashHandler(getMainController());
    }

    @Override
    public void loadServerConfiguration() {
        // do nothing
    }

    @Override
    public String applicationName() {
        return ResourceManager.getPackageInfo().packageName;
    }

    @Override
    public int versionCode() {
        return ResourceManager.getPackageInfo().versionCode;
    }

    @Override
    public String versionName() {
        return ResourceManager.getPackageInfo().versionName;
    }

    private class SimpleAndroidNetworkSystem extends AndroidNetworkSystem {

        public SimpleAndroidNetworkSystem(IController controller) {
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

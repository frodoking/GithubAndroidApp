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
import com.frodo.app.framework.net.NetworkInterceptor;
import com.frodo.app.framework.net.NetworkTransport;
import com.frodo.app.framework.net.Request;
import com.frodo.app.framework.scene.DefaultScene;
import com.frodo.app.framework.scene.Scene;
import com.frodo.app.framework.theme.Theme;

import java.io.File;

/**
 * Created by frodo on 2016/4/28.
 */
public class GithubApplicatioin extends MicroApplication {

    @Override
    public void init() {
        super.init();
        Fresco.initialize(this);
        getMainController().getLogCollector().enableCollect(true);
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
                return 0xff00fe;
            }
        };
    }

    @Override
    public NetworkTransport loadNetworkTransport() {
        return new SimpleAndroidNetworkSystem(getMainController(), null, null);
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

        public SimpleAndroidNetworkSystem(IController controller, final String userAccount, final String userPasswordSha1) {
            super(controller);
            setAPIUrl(controller.getConfig().getCurrentEnvironment().getUrl());
            addInterceptor(new NetworkInterceptor.RequestInterceptor() {
                @Override
                public Void intercept(Request request) {
                    Environment env = getMainController().getConfig().getCurrentEnvironment();
                    // FIXME: 2016/4/28
                    return super.intercept(request);
                }
            });
        }
    }
}

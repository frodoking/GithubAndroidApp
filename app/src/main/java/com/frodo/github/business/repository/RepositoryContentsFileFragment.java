package com.frodo.github.business.repository;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.dto.response.Content;
import com.frodo.github.view.CircleProgressDialog;
import com.frodo.github.view.ViewProvider;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/6/8.
 */
public class RepositoryContentsFileFragment extends StatedFragment<RepositoryContentsFileView, RepositoryModel> {
    private Content content;

    @Override
    public RepositoryContentsFileView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new RepositoryContentsFileView(this, inflater, container);
    }

    @Override
    public void onFirstTimeLaunched() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("content")) {
            content = bundle.getParcelable("content");
            getUIView().showPath(content.path);
            String repoFullName = content.url.substring(content.url.indexOf("repos/"), content.url.indexOf("/contents"));
            String[] repo = repoFullName.replace("repos/", "").replace("/contents", "").split("/");
            loadContentsWithReactor(repo[0], repo[1], content.path, content.url.split("\\?")[1].replace("ref=", ""));
        }
    }

    private void loadContentsWithReactor(String ownerName, String repName, final String path, final String ref) {
        getModel().loadContentWithReactor(ownerName, repName, path, ref)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        CircleProgressDialog.showLoadingDialog(getAndroidContext());
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Content>() {
                               @Override
                               public void call(Content content) {
                                   getUIView().hideEmptyView();
                                   getUIView().showContent(new String(Base64.decode(content.content, Base64.DEFAULT)));
                                   CircleProgressDialog.hideLoadingDialog();
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                CircleProgressDialog.hideLoadingDialog();
                                getUIView().showErrorView(ViewProvider.handleError(getMainController().getConfig().isDebug(), throwable));
                            }
                        });
    }
}

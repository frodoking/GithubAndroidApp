package com.frodo.github.business.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.view.CircleProgressDialog;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/5/5.
 */
public class LoginFragment extends StatedFragment<LoginView, AccountModel> {
    @Override
    public LoginView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new LoginView(this, inflater, container);
    }

    public void loginWithReactor(final String username, final String password) {
        getModel().loginUserWithReactor(username, password)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        CircleProgressDialog.showLoadingDialog(getAndroidContext());
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                               @Override
                               public void call(User user) {
                                   CircleProgressDialog.hideLoadingDialog();
                                   getMainController().getLocalBroadcastManager().onBroadcast("drawer", user);
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                CircleProgressDialog.hideLoadingDialog();
                                throwable.printStackTrace();
                            }
                        });
    }
}

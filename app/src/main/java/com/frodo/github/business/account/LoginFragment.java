package com.frodo.github.business.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.User;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
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
        Observable
                .create(new Observable.OnSubscribe<User>() {
                    @Override
                    public void call(Subscriber<? super User> subscriber) {
                        getModel().login(username, password, subscriber);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<User>() {
                            @Override
                            public void call(User result) {
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                            }
                        }
                );
    }
}

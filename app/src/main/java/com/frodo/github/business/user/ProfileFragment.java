package com.frodo.github.business.user;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.R;
import com.frodo.github.bean.dto.response.User;
import com.frodo.github.business.account.AccountModel;
import com.frodo.github.view.CircleProgressDialog;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/5/7.
 */
public class ProfileFragment extends StatedFragment<ProfileView, UserModel> {

    private User user;
    private String username;
    private AccountModel accountModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public ProfileView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new ProfileView(this, inflater, container);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFirstTimeLaunched() {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("username")) {
            username = bundle.getString("username");
            loadUserWithReactor(username);
        }
        accountModel = getMainController().getModelFactory()
                .getOrCreateIfAbsent(AccountModel.TAG, AccountModel.class, getMainController());
    }

    @Override
    public void onSaveState(Bundle outState) {
        outState.putParcelable("user", user);
    }

    @Override
    public void onRestoreState(Bundle savedInstanceState) {
        user = savedInstanceState.getParcelable("user");
        getUIView().showDetail(user, accountModel.isSignIn());
    }

    @Override
    public void onResume() {
        super.onResume();
        String usernameCurr = username;
        if (TextUtils.isEmpty(usernameCurr)) {
            if (user != null) {
                usernameCurr = user.login;
            }
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(usernameCurr);
    }

    public void loadUserWithReactor(final String username) {
        getModel().loadUserWithReactor(username)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        getUIView().showEmptyView();
                        CircleProgressDialog.showLoadingDialog(getAndroidContext());
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<User>() {
                               @Override
                               public void call(User user) {
                                   CircleProgressDialog.hideLoadingDialog();
                                   ProfileFragment.this.user = user;
                                   getUIView().hideEmptyView();
                                   getUIView().showDetail(user, accountModel.isSignIn());
                               }
                           },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                CircleProgressDialog.hideLoadingDialog();
                                getUIView().showErrorView(throwable);
                            }
                        });
    }
}

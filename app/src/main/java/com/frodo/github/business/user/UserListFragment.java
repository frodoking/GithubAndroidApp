package com.frodo.github.business.user;

import com.frodo.github.bean.dto.response.User;
import com.frodo.github.business.DevelopersAdapter;
import com.frodo.github.business.SearchListFragment;
import com.frodo.github.view.BaseListViewAdapter;
import com.frodo.github.view.CircleProgressDialog;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by frodo on 2016/5/31.
 */
public class UserListFragment extends SearchListFragment<UserModel, User> {

    @Override
    protected UserModel createModel() {
        return getMainController().getModelFactory().getOrCreateIfAbsent(UserModel.TAG, UserModel.class, getMainController());
    }

    @Override
    public BaseListViewAdapter<User> uiViewAdapter() {
        return new DevelopersAdapter(getAndroidContext());
    }

    @Override
    public void doSearch(String searchKey) {
    }

    @Override
    public void onFirstTimeLaunched() {
        getModel().loadUsers().subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        CircleProgressDialog.showLoadingDialog(getAndroidContext());
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<User>>() {
                               @Override
                               public void call(List<User> users) {
                                   CircleProgressDialog.hideLoadingDialog();
                                   setStateBeans((ArrayList<User>) users);
                                   getUIView().showList(users);
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

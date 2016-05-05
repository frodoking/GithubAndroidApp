package com.frodo.github.business.account;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;

/**
 * Created by frodo on 2016/5/5.
 */
public class LoginFragment extends StatedFragment<LoginView, AccountModel> {
    @Override
    public LoginView createUIView(Context context, LayoutInflater inflater, ViewGroup container) {
        return new LoginView(this, inflater, container);
    }

    @Override
    public AccountModel createModel() {
        return new AccountModel(getMainController());
    }
}

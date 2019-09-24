package com.frodo.github.business.user;

import android.annotation.SuppressLint;
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
import com.frodo.github.view.ViewProvider;
import com.mikepenz.octicons_typeface_library.Octicons;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by frodo on 2016/5/7.
 */
public class ProfileFragment extends StatedFragment<ProfileView, UserModel>
{

    private User user;

    private String username;

    private AccountModel accountModel;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public ProfileView createUIView(Context context, LayoutInflater inflater, ViewGroup container)
    {
        return new ProfileView(this, inflater, container);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_profile, menu);
        updateMenu(menu);
    }

    private void updateMenu(Menu menu)
    {
        ViewProvider.updateMenuItem(getAndroidContext(), menu, R.id.action_events, Octicons.Icon.oct_code);
        ViewProvider
                .updateMenuItem(getAndroidContext(), menu, R.id.action_organizations, Octicons.Icon.oct_organization);
        ViewProvider.updateMenuItem(getAndroidContext(), menu, R.id.action_repositories, Octicons.Icon.oct_repo);
        ViewProvider.updateMenuItem(getAndroidContext(), menu, R.id.action_gists, Octicons.Icon.oct_pulse);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @Override public void onFirstTimeLaunched()
    {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("username"))
        {
            username = bundle.getString("username");
            loadUserWithReactor(username);
        }
        accountModel = getMainController().getModelFactory()
                .getOrCreateIfAbsent(AccountModel.TAG, AccountModel.class, getMainController());
    }

    @Override public void onSaveState(Bundle outState)
    {
        outState.putParcelable("user", user);
    }

    @Override public void onRestoreState(Bundle savedInstanceState)
    {
        user = savedInstanceState.getParcelable("user");
        getUIView().showDetail(user, accountModel.isSignIn());
    }

    @Override public void onResume()
    {
        super.onResume();
        String usernameCurr = username;
        if (TextUtils.isEmpty(usernameCurr))
        {
            if (user != null)
            {
                usernameCurr = user.login;
            }
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(usernameCurr);
    }

    @SuppressLint ("CheckResult") public void loadUserWithReactor(final String username)
    {
        getModel().loadUserWithReactor(username).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>()
        {
            @Override public void accept(Disposable disposable)
            {
                getUIView().showEmptyView();
                CircleProgressDialog.showLoadingDialog(getAndroidContext());
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<User>()
        {
            @Override public void accept(User user)
            {
                CircleProgressDialog.hideLoadingDialog();
                ProfileFragment.this.user = user;
                getUIView().hideEmptyView();
                getUIView().showDetail(user, accountModel.isSignIn());
            }
        }, new Consumer<Throwable>()
        {
            @Override public void accept(Throwable throwable)
            {
                CircleProgressDialog.hideLoadingDialog();
                getUIView()
                        .showErrorView(ViewProvider.handleError(getMainController().getConfig().isDebug(), throwable));
            }
        });
    }

    public void doFollow(String username)
    {
        getModel().doFollow(username).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe();
    }
}

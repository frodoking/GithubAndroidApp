package com.frodo.github.business.account;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.UIView;
import com.frodo.app.framework.toolbox.TextUtils;
import com.frodo.github.R;

/**
 * Created by frodo on 2016/5/5.
 */
public class LoginView extends UIView {
	private TextInputLayout usernameWrapper;
	private TextInputLayout passwordWrapper;
	private Button loginBtn;

	public LoginView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
		super(presenter, inflater, container, R.layout.uiview_login);
	}

	@Override
	public void initView() {
		usernameWrapper = (TextInputLayout) getRootView().findViewById(R.id.usernameWrapper);
		passwordWrapper = (TextInputLayout) getRootView().findViewById(R.id.passwordWrapper);
		loginBtn = (Button) getRootView().findViewById(R.id.login_btn);
	}

	@Override
	public void registerListener() {
		loginBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hideKeyboard();
				String username = usernameWrapper.getEditText().getText().toString().trim();
				String password = passwordWrapper.getEditText().getText().toString().trim();
				if (!validateEmail(username)) {
					usernameWrapper.setError("Not a valid email address!");
					return;
				} else {
					usernameWrapper.setErrorEnabled(false);
				}
				if (!validatePassword(password)) {
					passwordWrapper.setError("Not a valid password!");
					return;
				} else {
					passwordWrapper.setErrorEnabled(false);
				}
				doLogin(username, password);
			}
		});
	}

	@Override
	public void onShowOrHide(final boolean isShown) {
		getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
	}

	private boolean validateEmail(String email) {
		return !TextUtils.isEmpty(email);
	}

	private boolean validatePassword(String password) {
		return !TextUtils.isEmpty(password);
	}

	private void doLogin(String username, String password) {
		((LoginFragment) getPresenter()).loginWithReactor(username, password);
	}

	private void hideKeyboard() {
		View view = ((Activity) getPresenter().getAndroidContext()).getCurrentFocus();
		if (view != null) {
			((InputMethodManager) getPresenter().getAndroidContext().getSystemService(Context.INPUT_METHOD_SERVICE)).
					hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
}

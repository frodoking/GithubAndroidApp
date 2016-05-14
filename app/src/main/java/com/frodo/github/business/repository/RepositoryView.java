package com.frodo.github.business.repository;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.UIView;
import com.frodo.github.R;
import com.frodo.github.bean.Repository;

/**
 * Created by frodo on 2016/5/7.
 */
public class RepositoryView extends UIView {
    public RepositoryView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.fragment_repository);
    }

    @Override
    public void initView() {
    }

    @Override
    public void registerListener() {

    }

    public void showDetail(Repository repository) {
    }

    public void showError(String message) {
        if (isOnShown()) {
            Toast.makeText(getPresenter().getAndroidContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}

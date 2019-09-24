package com.frodo.github.business.explore;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.frodo.app.android.ui.fragment.StatedFragment;
import com.frodo.github.bean.ShowCase;
import com.frodo.github.view.CircleProgressDialog;
import com.frodo.github.view.ViewProvider;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by frodo on 2016/5/3.
 */
public class ShowCaseDetailFragment extends StatedFragment<ShowCaseDetailView, ShowCaseDetailModel>
{
    private String tag = "";

    private ShowCase showCase;

    @Override public ShowCaseDetailView createUIView(Context context, LayoutInflater inflater, ViewGroup container)
    {
        return new ShowCaseDetailView(this, inflater, container);
    }

    @Override public void onFirstTimeLaunched()
    {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("slug"))
        {
            tag = bundle.getString("slug");
            loadShowCasesWithReactor(tag);
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(tag());
    }

    @Override public void onSaveState(Bundle outState)
    {
        outState.putParcelable("showcase", showCase);
    }

    @Override public void onRestoreState(Bundle savedInstanceState)
    {
        showCase = savedInstanceState.getParcelable("showcase");
        getUIView().showShowCaseDetail(showCase);
    }

    @Override public String tag()
    {
        return "Explore/" + tag;
    }

    private void loadShowCasesWithReactor(final String slug)
    {
        getModel().loadShowCaseDetailWithReactor(slug).doOnSubscribe(new Consumer<Disposable>()
        {
            @Override public void accept(Disposable disposable)
            {
                getUIView().showEmptyView();
                CircleProgressDialog.showLoadingDialog(getAndroidContext());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<ShowCase>()
        {
            @Override public void accept(ShowCase showCase)
            {

                CircleProgressDialog.hideLoadingDialog();
                ShowCaseDetailFragment.this.showCase = showCase;
                getUIView().hideEmptyView();
                getUIView().showShowCaseDetail(showCase);
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
}

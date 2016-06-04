package com.frodo.github.business;

import android.animation.ObjectAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.github.R;
import com.frodo.github.view.BaseRecyclerViewAdapter;

import java.util.List;

/**
 * Created by frodo on 2016/5/31.
 */
public abstract class SearchUIListView<ItemBean> extends AbstractUIView {

    private EditText searchET;
    private RecyclerView resultRV;
    private BaseRecyclerViewAdapter<ItemBean, RecyclerView.ViewHolder> adapter;

    private int searchViewHeight;

    private ObjectAnimator mAnimatorSearch;
    private boolean isSearchETShown;
    private float mTouchSlop;

    public SearchUIListView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.view_list_with_search);
    }

    @Override
    public void initView() {
        searchET = (EditText) getRootView().findViewById(R.id.search_et);
        resultRV = (RecyclerView) getRootView().findViewById(R.id.result_rv);
        resultRV.setLayoutManager(new LinearLayoutManager(getPresenter().getAndroidContext()));

        adapter = adapter();
        resultRV.setAdapter(adapter);

        final ViewConfiguration configuration = ViewConfiguration.get(getPresenter().getAndroidContext());
        mTouchSlop = configuration.getScaledTouchSlop();
    }

    @Override
    public void registerListener() {
        searchET.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                searchET.getViewTreeObserver().removeOnPreDrawListener(this);
                searchViewHeight = searchET.getMeasuredHeight();
                return true;
            }
        });

        resultRV.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int tmpY;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                if (firstVisibleItem == 0) {
                    if (!isSearchETShown) {
                        showSearchView(true);
                    }
                } else {
                    if (tmpY > mTouchSlop && isSearchETShown) {
                        showSearchView(false);
                        tmpY = 0;
                    }

                    if (tmpY < -mTouchSlop && !isSearchETShown) {
                        showSearchView(true);
                        tmpY = 0;
                    }
                }

                if ((isSearchETShown && dy > 0) || (!isSearchETShown && dy < 0)) {
                    tmpY += dy;
                }
            }
        });
    }

    private void showSearchView(boolean tag) {
        isSearchETShown = tag;
        int margin = ResourceManager.getDimensionPixelSize(R.dimen.margin_middle);
        if (mAnimatorSearch != null && mAnimatorSearch.isRunning()) {
            mAnimatorSearch.cancel();
        }

        final int translationY = tag ? 0 : -(searchViewHeight + margin);
        mAnimatorSearch = ObjectAnimator.ofFloat(searchET, View.TRANSLATION_Y, searchET.getTranslationY(), translationY);
        mAnimatorSearch.setDuration(300);
        mAnimatorSearch.start();
    }

    @Override
    public void onShowOrHide(boolean isShown) {
        getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
    }

    public void showList(List<ItemBean> list) {
        adapter.refreshObjects(list);
    }

    public abstract BaseRecyclerViewAdapter adapter();
}
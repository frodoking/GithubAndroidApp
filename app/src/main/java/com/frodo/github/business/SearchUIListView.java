package com.frodo.github.business;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.frodo.app.android.core.AndroidUIViewController;
import com.frodo.app.android.core.UIView;
import com.frodo.app.android.core.toolbox.ResourceManager;
import com.frodo.github.R;
import com.frodo.github.view.BaseListViewAdapter;

import java.util.List;

/**
 * Created by frodo on 2016/5/31.
 */
public abstract class SearchUIListView<ItemBean> extends UIView {

    private EditText searchET;
    private ListView resultLV;
    private BaseListViewAdapter adapter;

    private int searchViewHeight;
    private boolean isSearchViewShown;

    private ObjectAnimator mAnimatorSearch;
    private ObjectAnimator mAnimatorList;
    private float mTouchSlop;

    public SearchUIListView(AndroidUIViewController presenter, LayoutInflater inflater, ViewGroup container) {
        super(presenter, inflater, container, R.layout.view_list_with_search);
    }

    @Override
    public void initView() {
        searchET = (EditText) getRootView().findViewById(R.id.search_et);
        resultLV = (ListView) getRootView().findViewById(R.id.result_lv);
        adapter = adapter();
        resultLV.setAdapter(adapter);

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
        resultLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemBean itemBean = (ItemBean) adapter.getItem(position);
                itemClick(itemBean);
            }
        });

        resultLV.setOnTouchListener(new View.OnTouchListener() {

            private float y;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        y = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float tmpY = event.getY();
                        if (tmpY - y > mTouchSlop) {
                            showSearchView(true);
                        } else if (y - tmpY > mTouchSlop) {
                            showSearchView(false);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return false;
            }
        });
    }

    private void showSearchView(boolean tag) {
        int margin = ResourceManager.getDimensionPixelSize(R.dimen.margin_middle);
        final int translationY = searchViewHeight;
        if (mAnimatorSearch != null && mAnimatorSearch.isRunning()) {
            mAnimatorSearch.cancel();
        }
        if (mAnimatorList != null && mAnimatorList.isRunning()) {
            mAnimatorList.cancel();
        }
        if (tag) {
            mAnimatorSearch = ObjectAnimator.ofFloat(searchET, "translationY", searchET.getTranslationY(), 0);
            mAnimatorList = ObjectAnimator.ofFloat(resultLV, "translationY", resultLV.getTranslationY(), 0);
        } else {
            mAnimatorSearch = ObjectAnimator.ofFloat(searchET, "translationY", searchET.getTranslationY(), -(translationY + margin));
            mAnimatorList = ObjectAnimator.ofFloat(resultLV, "translationY", resultLV.getTranslationY(), -(translationY + margin * 2));
        }
        mAnimatorSearch.start();
        mAnimatorList.start();
    }

    @Override
    public void onShowOrHide(boolean isShown) {
        getPresenter().getModel().getMainController().getLocalBroadcastManager().onBroadcast("drawer", !isShown);
    }

    public void showList(List<ItemBean> list) {
        adapter.refreshObjects(list);
    }

    public abstract BaseListViewAdapter<ItemBean> adapter();

    public abstract void itemClick(ItemBean itemBean);
}
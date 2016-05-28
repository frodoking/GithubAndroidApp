package com.frodo.github.datasource;

import android.view.View;

import com.frodo.github.icon.R;

/**
 * Created by frodo on 2016/5/13.
 */
public class JsoupApi {

    public int getLayoutId() {
        return R.layout.jsoup_api_layout;
    }

    public void initView(View view) {
        view.findViewById(R.id.popular_repositories_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        WebUser.parse("http://github.com", "frodoking",);
                    }
                }).start();
            }
        });
    }
}

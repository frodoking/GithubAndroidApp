package com.frodo.github.datasource;

import android.content.Context;
import android.view.View;

import com.frodo.github.StaticFragment;
import com.frodo.github.icon.R;

/**
 * Created by frodo on 2016/5/13.
 */
public class JsoupApi extends StaticFragment {

    public JsoupApi(Context context) {
        super(context);
    }

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

    @Override
    public String tag() {
        return "JsoupApi";
    }
}

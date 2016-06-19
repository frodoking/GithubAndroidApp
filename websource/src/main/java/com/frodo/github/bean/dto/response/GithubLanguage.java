package com.frodo.github.bean.dto.response;

import java.io.Serializable;

/**
 * Created by frodo on 16/6/19.
 */
public class GithubLanguage implements Serializable {
    public String name;
    public String slug;

    public GithubLanguage(){}

    public GithubLanguage(String name, String slug) {
        this.name = name;
        this.slug= slug;
    }

    @Override
    public String toString() {
        return name;
    }
}

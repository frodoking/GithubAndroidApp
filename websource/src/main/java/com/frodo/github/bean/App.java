package com.frodo.github.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by frodo on 2016/5/5.
 */
public class App {
    @JsonProperty("url")
    public String url;
    @JsonProperty("name")
    public String name;
    @JsonProperty("client_id")
    public String clientId;
}

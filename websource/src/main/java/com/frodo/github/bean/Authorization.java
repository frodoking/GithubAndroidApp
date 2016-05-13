package com.frodo.github.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by frodo on 2016/5/5.
 */
public class Authorization {
    @JsonProperty("id")
    public int id;
    @JsonProperty("url")
    public String url;
    @JsonProperty("scopes")
    public String[] scopes;
    @JsonProperty("token")
    public String token;
    @JsonProperty("token_last_eight")
    public String tokenLastEight;
    @JsonProperty("hashed_token")
    public String hashedToken;
    @JsonProperty("app")
    public App app;
    @JsonProperty("note")
    public String note;
    @JsonProperty("note_url")
    public String noteUrl;
    @JsonProperty("updatedAt")
    public String updated_at;
    @JsonProperty("created_at")
    public String createdAt;
    @JsonProperty("fingerprint")
    public String fingerprint;
}

package com.frodo.github.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by frodo on 2016/5/14.
 */
public class Content {
    @JsonProperty("type")
    public String type;
    @JsonProperty("encoding")
    public String encoding;
    @JsonProperty("size")
    public int size;
    @JsonProperty("name")
    public String name;
    @JsonProperty("path")
    public String path;
    @JsonProperty("content")
    public String content;
    @JsonProperty("sha")
    public String sha;
    @JsonProperty("url")
    public String url;
    @JsonProperty("git_url")
    public String gitUrl;
    @JsonProperty("html_url")
    public String htmlUrl;
    @JsonProperty("download_url")
    public String downloadUrl;
}

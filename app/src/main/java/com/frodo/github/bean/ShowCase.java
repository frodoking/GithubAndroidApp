package com.frodo.github.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by frodo on 2016/4/30.
 */
public class ShowCase {
    @JsonProperty("name")
    public String name;
    @JsonProperty("slug")
    public String slug;
    @JsonProperty("description")
    public String description;
    @JsonProperty("image_url")
    public String imageUrl;
}

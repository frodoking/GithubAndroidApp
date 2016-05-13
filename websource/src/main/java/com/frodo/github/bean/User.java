package com.frodo.github.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

/**
 * Created by frodo on 2016/5/1.
 */
public class User extends Organization {
    @JsonProperty("name")
    public String name;
    @JsonProperty("company")
    public String company;
    @JsonProperty("blog")
    public String blog;
    @JsonProperty("location")
    public String location;
    @JsonProperty("email")
    public String email;
    @JsonProperty("hireable")
    public boolean hireable;
    @JsonProperty("bio")
    public String bio;
    @JsonProperty("public_repos")
    public int publicRepos;
    @JsonProperty("public_gists")
    public String publicGists;
    @JsonProperty("followers")
    public int followers;
    @JsonProperty("following")
    public int following;
    @JsonProperty("created_at")
    public Date createdAt;
    @JsonProperty("updated_at")
    public Date updatedAt;

    public int starred;
    public List<Repository> popularRepositories;
    public List<Repository> contributeToRepositories;
}

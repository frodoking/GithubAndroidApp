package com.frodo.github.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by frodo on 2016/5/1.
 */
public class Repository implements Serializable {
    public long id;
    public String name;
    public String full_name;
    public User owner;
    @JsonProperty("private")
    public boolean isPrivate;
    public String html_url;
    public String description;
    public boolean fork;
    public String url;
    public String forks_url;
    public String keys_url;
    public String collaborators_url;
    public String teams_url;
    public String hooks_url;
    public String issue_events_url;
    public String events_url;
    public String assignees_url;
    public String branches_url;
    public String tags_url;
    public String blobs_url;
    public String git_tags_url;
    public String git_refs_url;
    public String trees_url;
    public String statuses_url;
    public String languages_url;
    public String stargazers_url;
    public String contributors_url;
    public String subscribers_url;
    public String subscription_url;
    public String commits_url;
    public String git_commits_url;
    public String comments_url;
    public String issue_comment_url;
    public String contents_url;
    public String compare_url;
    public String merges_url;
    public String archive_url;
    public String downloads_url;
    public String issues_url;
    public String pulls_url;
    public String milestones_url;
    public String notifications_url;
    public String labels_url;
    public String releases_url;
    public String deployments_url;
    public Date created_at;
    public Date updated_at;
    public Date pushed_at;
    public String git_url;
    public String ssh_url;
    public String clone_url;
    public String svn_url;
    public String homepage;
    public int size;
    public int stargazers_count;
    public int watchers_count;
    public String language;
    public boolean has_issues;
    public boolean has_downloads;
    public boolean has_wiki;
    public boolean has_pages;
    public int forks_count;
    public String mirror_url;
    public int open_issues_count;
    public int forks;
    public int open_issues;
    public int watchers;
    public String default_branch;
    public Organization organization;
    public int network_count;
    public int subscribers_count;
    public Repository parent;
    public Repository source;
}

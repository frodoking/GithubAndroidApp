package com.frodo.github.datasource;

import com.frodo.github.bean.Repository;
import com.frodo.github.bean.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by frodo on 2016/5/13.
 */
public class WebUser {

    public static User parse(String host, String user) {
        try {
            User userWebInfo = null;
            Document doc = Jsoup.connect(host + '/' + user).get();
            Elements popularReposElems = doc.select("div[class=columns popular-repos]");
            String starred = doc.select("a[class=vcard-stat]").get(1).select("strong[class=vcard-stat-count d-block]").text();
            if (popularReposElems != null) {
                userWebInfo = new User();
                if (starred != null) {
                    userWebInfo.starred = Integer.valueOf(starred);
                }
                Elements columnElems = popularReposElems.select("div[class=column one-half]");
                if (columnElems != null) {
                    for (int i = 0; i < columnElems.size(); i++) {
                        Element element = columnElems.get(i);
                        if (element != null) {
                            Elements repoElems = element.select("li[class=public source ]");
                            if (repoElems != null) {
                                List<Repository> repos = new ArrayList<>(repoElems.size());
                                for (int j = 0; j < repoElems.size(); j++) {
                                    Element elementRepo = repoElems.get(j);
                                    if (elementRepo != null) {
                                        Repository repo = new Repository();
                                        repo.archive_url = elementRepo.getElementsByTag("a").get(0).attr("href");

                                        Elements elementRepoString = elementRepo.select("span[class=repo]");
                                        repo.name = elementRepoString.get(0).text();

                                        Elements elementStars = elementRepo.select("span[class=stars]");
                                        repo.stargazers_count = Integer.parseInt(elementStars.get(0).text().replace(",", ""));

                                        Elements elementRepoDescription = elementRepo.select("span[class=repo-description css-truncate-target]");
                                        repo.description = elementRepoDescription.get(0).text();
                                        repos.add(repo);
                                    }
                                }
                                if (i == 0) {
                                    userWebInfo.popularRepositories = repos;
                                } else if (i == 1) {
                                    userWebInfo.contributeToRepositories = repos;
                                }
                            }
                        }
                    }
                }
            }
            return userWebInfo;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

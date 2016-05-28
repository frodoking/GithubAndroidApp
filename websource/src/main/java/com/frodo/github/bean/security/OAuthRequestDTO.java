package com.frodo.github.bean.security;

import java.util.List;

/**
 * Created by Bernat on 08/07/2014.
 */
public class OAuthRequestDTO {
    public List<String> scopes;
    public String note;
    public String client_id;
    public String client_secret;
}

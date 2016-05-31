package com.frodo.github.business.account;

import com.frodo.app.framework.cache.AbstractCache;
import com.frodo.app.framework.cache.CacheSystem;
import com.frodo.github.bean.dto.response.User;

/**
 * Created by frodo on 2016/5/31.
 */
public class AccountCache extends AbstractCache<String, User> {
    public AccountCache(CacheSystem cacheSystem) {
        super(cacheSystem, Type.INTERNAL);
    }

    @Override
    public User get(String key) {
        return null;
    }

    @Override
    public void put(String key, User value) {

    }

    @Override
    public boolean isCached(String key) {
        return false;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public void evictAll() {

    }
}

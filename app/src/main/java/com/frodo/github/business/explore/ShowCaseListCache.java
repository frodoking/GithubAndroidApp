package com.frodo.github.business.explore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.frodo.app.android.core.toolbox.HashUtils;
import com.frodo.app.framework.cache.AbstractCache;
import com.frodo.app.framework.cache.CacheSystem;
import com.frodo.app.framework.filesystem.FileSystem;
import com.frodo.app.framework.log.Logger;
import com.frodo.github.bean.ShowCase;

import java.io.File;
import java.util.List;

/**
 * Created by frodo on 2016/4/30.
 */
public class ShowCaseListCache extends AbstractCache<String, List<ShowCase>> {

    public ShowCaseListCache(CacheSystem cacheSystem, Type type) {
        super(cacheSystem, type);
    }

    @Override
    public List<ShowCase> get(String key) {
        if (isCached(key)) {
            return getCacheSystem().findCacheFromDisk(createAbsoluteKey(key), new TypeReference<List<ShowCase>>() {
            });
        }
        return null;
    }

    @Override
    public void put(String key, List<ShowCase> value) {
        getCacheSystem().put(createAbsoluteKey(key), value, getType());
    }

    @Override
    public boolean isCached(String key) {
        if (getType().equals(Type.DISK)) {
            return getCacheSystem().existCacheInDisk(createAbsoluteKey(key));
        }
        return false;
    }

    @Override
    public boolean isExpired() {
        return false;
    }

    @Override
    public void evictAll() {
        getCacheSystem().evict("");
    }

    private String createAbsoluteKey(String relativeKey) {
        final FileSystem fs = getCacheSystem().getController().getFileSystem();
        final String absoluteKey = fs.getFilePath() + File.separator + getCacheKey(relativeKey) + ".cache.tmp";
        Logger.fLog().tag("MovieCache").d("Cache path >>>> " + absoluteKey);
        return absoluteKey;
    }

    private String getCacheKey(String cacheKey) {
        return HashUtils.computeWeakHash(cacheKey.trim()) + String.format("%04x", cacheKey.length());
    }
}


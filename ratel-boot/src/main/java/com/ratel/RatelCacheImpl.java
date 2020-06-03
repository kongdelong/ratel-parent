package com.ratel;

import com.ratel.framework.cache.RatelCache;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RatelCacheImpl implements RatelCache {
    @Override
    public void del(String key) {

    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public List<String> scan(String s) {
        return null;
    }

    @Override
    public void set(String s, Object object, long l) {

    }

    @Override
    public long getExpire(String s) {
        return 0;
    }

    @Override
    public void expire(String s, long renew, TimeUnit milliseconds) {

    }
}

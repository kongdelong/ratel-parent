package com.ratel;

import com.ratel.framework.modules.cache.RatelCacheProvider;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

//@Service
public class RatelCacheImpl implements RatelCacheProvider {


    @Override
    public boolean expire(String key, long time) {
        return false;
    }

    @Override
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        return false;
    }

    @Override
    public long getExpire(Object key) {
        return 0;
    }

    @Override
    public boolean set(String key, Object value) {
        return false;
    }

    @Override
    public boolean set(String key, Object value, long time) {
        return false;
    }

    @Override
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        return false;
    }

    @Override
    public Object get(String key) {
        return null;
    }

    @Override
    public void del(String... key) {

    }

    @Override
    public List<String> scan(String pattern) {
        return null;
    }
}

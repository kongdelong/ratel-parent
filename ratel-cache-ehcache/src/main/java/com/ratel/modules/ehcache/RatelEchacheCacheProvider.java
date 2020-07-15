package com.ratel.modules.ehcache;

import cn.hutool.core.lang.Assert;
import com.ratel.framework.modules.cache.RatelCacheProvider;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@SuppressWarnings({"unchecked", "all"})
public class RatelEchacheCacheProvider implements RatelCacheProvider {

    private static final String SYS_CACHE = "sysCache";
    private static final int DEF_EXPIRATION = 30 * 24 * 60 * 60; // 30天

    protected net.sf.ehcache.Cache cache;

    protected static CacheManager manager = CacheManager.getInstance();

    /**
     * 构造方法
     */
    public RatelEchacheCacheProvider() {
        this.cache = getCache(SYS_CACHE);
    }

    /**
     * 构造方法
     *
     * @param cacheName 缓存的名称
     */
    public RatelEchacheCacheProvider(String cacheName) {
        this.cache = getCache(cacheName);
    }

    protected net.sf.ehcache.Cache getCache(String name) {
        if (!manager.cacheExists(name)) {
            synchronized (manager) {// 此处采用锁，因为可能初次访问一个cache时候都还未创建同时创建至于文件被锁,此处为了解决这个
                if (!manager.cacheExists(name)) {
                    try {
                        manager.addCache(name);
                    } catch (CacheException ce) {
                        log.error("EhCacheEngine.getCache", ce);
                    }
                }
            }
        }
        return manager.getCache(name);
    }

    /**
     * [将{key:value}存到缓存{cacheName}中,存活时间{timeToLiveSeconds}秒,钝化时间{timeToIdleSeconds}秒]
     */
    public void set(String cacheName, String key, Object value, int timeToLiveSeconds, int timeToIdleSeconds) {
        Assert.notBlank(key, "缓存Key非法!");
        synchronized (key.intern()) {
            Element element = new Element(key, value);
            element.setEternal(false);
            element.setTimeToLive(timeToLiveSeconds);
            element.setTimeToIdle(timeToIdleSeconds);
            cache.put(element);
        }
    }

    public synchronized void setSynchronized(String key, Object value, int expiration) {
//        log.debug(String.format("EhCachedProvider.set: key = %s value = %s expr = %d", key, value.toString(), expiration));
        try {
            cache.remove(key);
            Element element = new Element(key, value);
            element.setTimeToLive(expiration);
            cache.put(element);
        } catch (Exception e) {
            log.error("EhCachedProvider.set", e);
        }
    }


    public void remove(String key) {
//        log.debug("EhCachedProvider.remove:" + key);
        try {
            cache.remove(key);
        } catch (Exception e) {
            log.error("EhCachedProvider.remove", e);
        }
    }

    public synchronized void replace(String key, Object value, int expiration) {
//        log.debug(String.format("EhCachedProvider.replace: key = %s value = %s expr = %d", key, value.toString(), expiration));
        try {
            Element element = new Element(key, value);
            element.setTimeToLive(0);
            element.setTimeToIdle(expiration);
            cache.replace(element);
        } catch (Exception e) {
            log.error("EhCachedProvider.replace", e);
        }
    }

    public void flush() {
//        log.debug("EhCachedProvider.flushAll");
        try {
            cache.removeAll();
        } catch (Exception e) {
            log.error("EhCachedProvider.flushAll", e);
        }
    }

    public String getCounter(String key) {
        Object o = get(key);
        String counter = "0";
        if (StringUtils.isEmpty(o)) {
            set(key, counter, DEF_EXPIRATION);
        } else {
            counter = o.toString();
        }
        return counter;
    }

    public void flush(String type, String key) {
        String actionKey = type + "_" + key;
        int counter = Integer.valueOf(getCounter(actionKey));
        set(actionKey, counter + 1, DEF_EXPIRATION);
    }

    @Override
    public Object get(String key) {
        Element element = cache.get(key);
        if (element == null) {
            return null;
        }
        return element.getObjectValue();
    }

    @Override
    public void del(String... keys) {
        for (String key : keys) {
            this.remove(key);
        }
    }

    @Override
    public List<String> scan(String pattern) {
        return null;
    }


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

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    @Override
    public boolean set(String key, Object value) {
        try {
            this.set(key, value, DEF_EXPIRATION);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean set(String key, Object value, long time) {
        try {
            this.setSynchronized(key, value, new Long(time).intValue());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            this.set(key, value, time);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}

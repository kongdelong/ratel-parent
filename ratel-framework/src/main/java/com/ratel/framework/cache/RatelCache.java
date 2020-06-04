package com.ratel.framework.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface RatelCache {

    void del(String... key);

    Object get(String key);

    List<String> scan(String s);

    boolean set(String s, Object object, long l);

    long getExpire(Object key);

    boolean expire(String s, long renew, TimeUnit milliseconds);


    /**
     * 普通缓存放入并设置时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间
     * @param timeUnit 类型
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit);
}

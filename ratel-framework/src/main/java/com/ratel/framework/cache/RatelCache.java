package com.ratel.framework.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface RatelCache {

    void del(String key);

    Object get(String key);

    List<String> scan(String s);

    void set(String s, Object object, long l);

    long getExpire(String s);

    void expire(String s, long renew, TimeUnit milliseconds);
}

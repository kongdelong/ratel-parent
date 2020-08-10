package com.ratel.framework.modules.cache;

import java.util.List;
import java.util.concurrent.TimeUnit;

public interface RatelCacheProvider {

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     */
    public boolean expire(String key, long time);

    /**
     * 指定缓存失效时间
     *
     * @param key      键
     * @param time     时间(秒)
     * @param timeUnit 单位
     */
    public boolean expire(String key, long time, TimeUnit timeUnit);

    /**
     * 根据 key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire(Object key);

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value);

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time);

    /**
     * 普通缓存放入并设置时间
     *
     * @param key      键
     * @param value    值
     * @param time     时间
     * @param timeUnit 类型 redis
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit);

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key);

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void del(String key);

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    public void del(String name, String key);

    /**
     * 查找匹配key
     *
     * @param pattern key
     * @return /
     */
    public List<String> scan(String pattern);

}

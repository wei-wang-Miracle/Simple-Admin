package com.simple.core.cache;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类
 */
@Component
@RequiredArgsConstructor
public class CacheUtil {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 设置缓存（默认不过期）
     */
    public void set(String key, Object value) {
        if (value == null) {
            return;
        }
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value));
    }

    /**
     * 设置缓存（指定过期时间，单位秒）
     */
    public void set(String key, Object value, long seconds) {
        if (value == null) {
            return;
        }
        stringRedisTemplate.opsForValue().set(key, JSON.toJSONString(value), seconds, TimeUnit.SECONDS);
    }

    /**
     * 获取缓存
     */
    public <T> T get(String key, Class<T> clazz) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isEmpty(value)) {
            return null;
        }
        return JSON.parseObject(value, clazz);
    }

    /**
     * 删除缓存
     */
    public void del(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 判断缓存是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * 设置过期时间
     */
    public void expire(String key, long seconds) {
        stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
    }


}

package com.hm.seckill.redis;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Service
public class RedisService {

    @Autowired
    JedisPool jedisPool;


    // 获取对象
    public <T> T get(KeyPrefix keyPrefix, String key, Class<T> clazz){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();

            // 生成真正的key
            String realKey = keyPrefix.getPrefix() + key;
            String str = jedis.get(realKey);
            T t = stringToBean(str, clazz);
            return t;
        }finally {
            returnToPool(jedis);
        }
    }

    // 设置对象
    public <T> boolean set(KeyPrefix keyPrefix, String key, T value){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String str = beanToString(value);

            if (str == null || str.length() <= 0)
                return false;

            // 生成真正的key
            String realKey = keyPrefix.getPrefix() + key;

            // 判断过期时间
            int seconds = keyPrefix.expireSeconds();
            if (seconds <= 0){  // 永不过期
                jedis.set(realKey,str);
            }else{
                jedis.setex(realKey, seconds, str); // 带有效期的kv
            }

            return true;
        }finally {
            returnToPool(jedis);
        }
    }

    // 判断key是否存在
    public <T> boolean exists(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();

            // 生成真正的key
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.exists(realKey);
        }finally {
            returnToPool(jedis);
        }
    }

    // 删除
    public boolean delete(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();

            // 生成真正的key
            String realKey = keyPrefix.getPrefix() + key;
            long ret = jedis.del(realKey);
            return ret > 0;
        }finally {
            returnToPool(jedis);
        }
    }

    // 增加值，将 key 中储存的数字值增一。
    // 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作
    public <T> Long incr(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.incr(realKey); // 原子操作
        }finally {
            returnToPool(jedis);
        }
    }

    // 减少值
    public <T> Long decr(KeyPrefix keyPrefix, String key){
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            String realKey = keyPrefix.getPrefix() + key;
            return jedis.decr(realKey);
        }finally {
            returnToPool(jedis);
        }
    }



    public static <T> String beanToString(T value) {
        if (value == null)
            return null;

        Class<?> clazz = value.getClass();

        if (clazz == int.class || clazz == Integer.class){
            return value + "";
        }else if (clazz == String.class){
            return (String)value;
        }else if (clazz == long.class || clazz == Long.class){
            return value + "";
        }else{
            return JSON.toJSONString(value);
        }
    }

    public static <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null){
            return null;
        }
        if (clazz == int.class || clazz == Integer.class){
            return (T) Integer.valueOf(str);
        }else if (clazz == String.class){
            return (T) str;
        }else if (clazz == long.class || clazz == Long.class){
            return (T) Long.valueOf(str);
        }else{
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    private void returnToPool(Jedis jedis) {
        if (jedis != null)
            jedis.close();
    }


}

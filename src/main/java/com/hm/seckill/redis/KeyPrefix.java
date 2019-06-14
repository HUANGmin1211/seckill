package com.hm.seckill.redis;

public interface KeyPrefix {

    public int expireSeconds();  // 有效期
    public String getPrefix();
}

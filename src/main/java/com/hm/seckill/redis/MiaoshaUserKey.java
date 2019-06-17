package com.hm.seckill.redis;

import com.hm.seckill.domain.MiaoshaUser;

public class MiaoshaUserKey extends BasePrefix {


    public static final int TOKEN_EXPORE = 3600*24*2;

    public MiaoshaUserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaUserKey token = new MiaoshaUserKey(TOKEN_EXPORE, "tk");
}

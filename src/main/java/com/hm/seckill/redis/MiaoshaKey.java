package com.hm.seckill.redis;

public class MiaoshaKey extends BasePrefix {

    public MiaoshaKey(String prefix) {
        super( prefix);
    }

    public static MiaoshaKey isGoodsOver = new MiaoshaKey("go");
}
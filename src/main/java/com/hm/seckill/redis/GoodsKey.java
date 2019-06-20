package com.hm.seckill.redis;

public class GoodsKey extends BasePrefix {

    // 禁止外部创建对象，userKey只能从这个类中取，不能在外面自定义
    private GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    // 页面缓存的时间都较短，我们设为1分钟
    // 页面缓存是为了防止秒杀瞬间访问量过大导致服务器压力太大，但如果缓存时间太长，数据的即时性就较弱。
    public static GoodsKey getGoodsList = new GoodsKey(60,"gl");
    public static GoodsKey getGoodsDetail = new GoodsKey(60,"gd");
}

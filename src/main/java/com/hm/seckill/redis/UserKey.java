package com.hm.seckill.redis;

public class UserKey extends BasePrefix {

    // 禁止外部创建对象，userKey只能从这个类中取，不能在外面自定义
    private UserKey(String prefix) {
        super(prefix);
    }

    public static UserKey getById = new UserKey("id");
    public static UserKey getByName = new UserKey("name");
}

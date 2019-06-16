package com.hm.seckill.util;

import com.alibaba.druid.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {

    private static final Pattern mobilt_patten = Pattern.compile("1\\d{10}");

    // 判断src是不是手机号
    public static boolean isMobile(String src){
        if (StringUtils.isEmpty(src))
            return false;
        Matcher m = mobilt_patten.matcher(src);
        return m.matches();
    }

    public static void main(String[] args) {
        System.out.println(isMobile("15866661234"));
        System.out.println(isMobile("1586666123"));
    }
}

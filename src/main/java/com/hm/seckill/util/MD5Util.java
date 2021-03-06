package com.hm.seckill.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    // 第一次salt用固定的salt，这个salt不被传输，所以服务端得自己知道，所以必须是固定的
    private static final String salt = "1a2b3c4d";

    // 第一次md5，防止明文密码在网络上传输
    public static String inputPassFormPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    // 第二次md5，防止万一数据库泄露即可得到第二次的salt，但是第一次的salt并不会泄露，除非源码泄露
    public static String formPassDBPass(String formPass, String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPadd(String input, String saltDB){
        String formPass = inputPassFormPass(input);
        String DBPass = formPassDBPass(formPass,saltDB);
        return DBPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassFormPass("123456"));  // d3b1294a61a07da9b49b6e22b2cbd7f9
        System.out.println(formPassDBPass(inputPassFormPass("123456"),"1a2b3c4d"));  // b7797cce01b4b131b433b6acf4add449
        System.out.println(inputPassToDBPadd("123456","1a2b3c4d"));
    }
}

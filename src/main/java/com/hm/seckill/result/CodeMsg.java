package com.hm.seckill.result;

public class CodeMsg {
    private int code;
    private String msg;

    // 这样我们把所有的codeMsg都按模块统一定义在这里，更改的时候仅修改这里即可
    // 而且仅需要对外暴露get方法，不允许外界随意修改，因此不提供set方法

    // 通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500100,"参数校验异常：%s");

    // 登陆模块 5002XX
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "Session不存在或者已经失效");
    public static CodeMsg PASSWORD_EMPTY = new CodeMsg(500211,"密码不能为空");
    public static CodeMsg MOBILE_EMPTY = new CodeMsg(500212,"手机号不能为空");
    public static CodeMsg MOBILE_ERROR = new CodeMsg(500213,"手机号码格式错误");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214,"手机号码不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215,"密码错误");

    // 商品模块 5003XX

    // 订单模块 5004XX
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400,"订单为空");


    // 秒杀模块 5005XX
    public static CodeMsg MIAOSHA_OVER = new CodeMsg(500500,"商品已经秒杀完毕");
    public static CodeMsg MIAOSHA_REPEAT = new CodeMsg(500501,"不能重复秒杀");


    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    // 带参 public static CodeMsg BIND_ERROR = new CodeMsg(500100,"参数校验异常：%s");
    public CodeMsg fillArgs(Object... args){
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "CodeMsg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}

package com.hm.seckill.result;

public class CodeMsg {
    private int code;
    private String msg;

    // 这样我们把所有的codeMsg都按模块统一定义在这里，更改的时候仅修改这里即可
    // 而且仅需要对外暴露get方法，不允许外界随意修改，因此不提供set方法

    // 通用异常
    public static CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务端异常");

    // 登陆模块 5002XX

    // 商品模块 5003XX

    // 订单模块 5004XX

    // 秒杀模块 5005XX

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}

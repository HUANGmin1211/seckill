package com.hm.seckill.result;

public class Result<T> {
    private int code;
    private String msg;
    private T data;

    /**
     * 成功时调用
     */
    public static <T> Result<T> success(T data){
        return new Result(data);
    }

    /**
     * 失败时调用
     */
    public static <T> Result<T> error(CodeMsg cm){
        return new Result(cm);
    }

    // 我们禁止外面直接通过构造函数来擅自生成对象，应该调用封装好的方法seccess和error
    // 因此把构造函数设为private
    private Result(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
    }

    private Result(CodeMsg cm) {
        if (cm == null)
            return;
        this.code = cm.getCode();
        this.msg = cm.getMsg();
    }


    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }

}

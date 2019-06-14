package com.hm.seckill.controller;

import com.hm.seckill.result.CodeMsg;
import com.hm.seckill.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoController {

    @RequestMapping("/")
    @ResponseBody
    public String home(){
        return "hello world";
    }

    @RequestMapping("/hello")
    @ResponseBody
    public Result<String> hello(Model model){
//        return new Result(0,"success","hello");  // 这样调用太不优雅了
        return Result.success("hello hm");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public Result<String> helloError(Model model){
//        return new Result(50010,"session失效");
//        return new Result(50020,"XXX");
        return Result.error(CodeMsg.SERVER_ERROR);
    }

    @RequestMapping("/thyemleaf")
    public String thymeleaf(Model model){
        model.addAttribute("name", "hm");
        return "hello";
    }
}

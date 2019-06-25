package com.hm.seckill.controller;

import com.hm.seckill.domain.User;
import com.hm.seckill.rabbitmq.MQSender;
import com.hm.seckill.redis.RedisService;
import com.hm.seckill.redis.UserKey;
import com.hm.seckill.result.CodeMsg;
import com.hm.seckill.result.Result;
import com.hm.seckill.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;

    @Autowired
    MQSender sender;

    @RequestMapping("/mq/header")
    @ResponseBody
    public Result<String> header() {
		sender.sendHeader("hello,imooc");
        return Result.success("Hello，world");
    }

	@RequestMapping("/mq/fanout")
    @ResponseBody
    public Result<String> fanout() {
		sender.sendFanout("hello,imooc");
        return Result.success("Hello，world");
    }

	@RequestMapping("/mq/topic")
    @ResponseBody
    public Result<String> topic() {
		sender.sendTopic("hello,imooc");
        return Result.success("Hello，world");
    }

	@RequestMapping("/mq")
    @ResponseBody
    public Result<String> mq() {
		sender.send("hello,imooc");
        return Result.success("Hello，world");
    }



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

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> DBGet(){
        User user = userService.getById(1);
        return Result.success(user);
    }

    @RequestMapping("/db/tx")   // 事务测试 @Transactional
    @ResponseBody
    public Result<Boolean> DBtx(){
        userService.tx();
        return Result.success(true);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<User> redisGet(){
        User user = redisService.get(UserKey.getById, ""+1, User.class); // key = UserKey:id1
        return Result.success(user);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet(){
        User user = new User(1,"1111");

        boolean ret = redisService.set(UserKey.getById, ""+1, user);
        return Result.success(true);
    }




}

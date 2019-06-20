package com.hm.seckill.controller;

import com.hm.seckill.domain.MiaoshaUser;
import com.hm.seckill.redis.RedisService;
import com.hm.seckill.result.Result;
import com.hm.seckill.service.GoodsService;
import com.hm.seckill.service.MiaoshaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    MiaoshaUserService userService;
    @Autowired
    RedisService redisService;

    // 压测测试获取user
    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> info(MiaoshaUser user) {
        return Result.success(user);
    }
}

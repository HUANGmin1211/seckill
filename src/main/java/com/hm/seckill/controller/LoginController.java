package com.hm.seckill.controller;

import com.alibaba.druid.util.StringUtils;
import com.hm.seckill.domain.MiaoshaUser;
import com.hm.seckill.result.CodeMsg;
import com.hm.seckill.result.Result;
import com.hm.seckill.service.MiaoshaUserService;
import com.hm.seckill.util.ValidatorUtil;
import com.hm.seckill.vo.LoginVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    MiaoshaUserService userService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse response, @Valid LoginVo loginVo){  // @Valid 参数校验
        log.info(loginVo.toString());

        /**
        // 参数校验,引入校验器，在参数初始化的地方通过注解进行校验，而不需要在每个用到的地方再用一大段代码进行校验
        String passInput = loginVo.getPassword();
        String mobile = loginVo.getMobile();

        if (StringUtils.isEmpty(passInput))
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        if (StringUtils.isEmpty(mobile))
            return Result.error(CodeMsg.MOBILE_EMPTY);
        if (!ValidatorUtil.isMobile(mobile))
            return Result.error(CodeMsg.MOBILE_ERROR);
        */


        // 登陆
        userService.login(response, loginVo);
        return Result.success(true);
        /* userService经过改造，出现异常就直接抛出了，无需返回condeMsg
        if (codeMsg.getCode() == 0)
            return Result.success(true);
        else
            return Result.error(codeMsg);
        * */


    }
}

package com.hm.seckill.service;

import com.alibaba.druid.util.StringUtils;
import com.hm.seckill.dao.MiaoshaUserDao;
import com.hm.seckill.domain.MiaoshaUser;
import com.hm.seckill.exception.GlobalException;
import com.hm.seckill.redis.MiaoshaUserKey;
import com.hm.seckill.redis.RedisService;
import com.hm.seckill.result.CodeMsg;
import com.hm.seckill.util.MD5Util;
import com.hm.seckill.util.UUIDUtil;
import com.hm.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Service
public class MiaoshaUserService {

    public static final String COOKIE_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(long id){
        return miaoshaUserDao.getBuId(id);
    }

    public boolean login(HttpServletResponse response, LoginVo loginVo){
        if (loginVo == null)
            throw new GlobalException(CodeMsg.SERVER_ERROR);

        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword(); // 已经进行过一次md5的密码

        // 判断手机号是否存在
        MiaoshaUser user = getById(Long.parseLong(mobile));
        if (user == null)
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);

        // 验证密码
        String dbPass = user.getPassword();
        String saltDB = user.getSalt();
        String calcPass = MD5Util.formPassDBPass(formPass, saltDB);
        if (!calcPass.equals(dbPass))
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);

        // 生成token和cookie
        addCookie(user, response);

        return true;
    }

    public MiaoshaUser getByToken( HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token))
            return null;

        MiaoshaUser user =  redisService.get(MiaoshaUserKey.token,token, MiaoshaUser.class);

        // 延长有效期：有效期应该是最后一次登陆的时间，加上有效期时间2天
        if (user != null)
            addCookie(user, response);
        return user;
    }

    // 生成token和cookie
    private void addCookie(MiaoshaUser user, HttpServletResponse response){
        String token = UUIDUtil.uuid();
        redisService.set(MiaoshaUserKey.token,token,user);
        Cookie cookie = new Cookie(COOKIE_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.expireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}

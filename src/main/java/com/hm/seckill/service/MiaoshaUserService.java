package com.hm.seckill.service;

import com.hm.seckill.dao.MiaoshaUserDao;
import com.hm.seckill.domain.MiaoshaUser;
import com.hm.seckill.exception.GlobalException;
import com.hm.seckill.result.CodeMsg;
import com.hm.seckill.util.MD5Util;
import com.hm.seckill.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MiaoshaUserService {

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    public MiaoshaUser getById(long id){
        return miaoshaUserDao.getBuId(id);
    }

    public boolean login(LoginVo loginVo){
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

        return true;
    }
}

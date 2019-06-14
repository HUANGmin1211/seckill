package com.hm.seckill.service;

import com.hm.seckill.dao.UserDao;
import com.hm.seckill.domain.User;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    public User getById(int id){
        return userDao.getById(id);
    }

    @Transactional // 事务注解
    public boolean tx(){
        User u2 = new User();
        u2.setId(2);
        u2.setName("222");
        userDao.insert(u2);

        // 测试事务。u1的id与原数据库中重复，两个user应该都无法插入
        User u1 = new User();
        u1.setId(1);
        u1.setName("111");
        userDao.insert(u1);

        return true;
    }
}

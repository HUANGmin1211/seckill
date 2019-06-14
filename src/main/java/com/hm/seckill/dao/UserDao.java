package com.hm.seckill.dao;

import com.hm.seckill.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface UserDao {

    @Select("select * from user where id = #{id}")
    public User getById(@Param("id") int id);

    @Insert("insert into user (id,name) values (#{id},#{name})")
    public int insert(User user);

}

package com.zyh.community.dao;

import com.zyh.community.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.jws.soap.SOAPBinding;

/**
 * @author
 * @Description
 * @create 2022-05-16 15:29
 */
@Mapper
public interface UserMapper {
    User selectById(int id);
    User selectByName(String name);
    User selectByEmail(String email);
    int insertUser(User user);
    int updateStatus(int id,int status);
    int updatePassword(@Param("id")int id,@Param("password")String password);
    int updateHeader(@Param("id")int id,@Param("headerUrl") String headerUrl);
}

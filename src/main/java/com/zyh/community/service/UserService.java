package com.zyh.community.service;

import com.zyh.community.dao.UserMapper;
import com.zyh.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author
 * @Description
 * @create 2022-05-16 19:24
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    public User findUserById(int id){
        return userMapper.selectById(id);
    }
}

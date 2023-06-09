package com.example.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.User;
import com.example.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
    @Autowired
    UserMapper userMapper;

    public User getByUserName(String userName) {
        return userMapper.getByUserName(userName);
    }

    public List<User> getUserByType(String type) {
        return userMapper.getUserByType(type);
    }
}


package com.example.timehelperserver.service;

import com.example.timehelperserver.dao.UserMapper;
import com.example.timehelperserver.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserService {

    @Resource
    private UserMapper userMapper;

    public String loginService(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            return "false";
        } else {
            if (password.equals(user.getPassword())) {
                return "true";
            } else {
                return "false";
            }
        }
    }

    public String registerService(User user) {
        String tempUnm = user.getUsername();
        User tempUser = userMapper.findByUsername(tempUnm);
        if (tempUser != null) {
            return "false";
        } else {
            userMapper.save(user);
            return "true";
        }
    }
}

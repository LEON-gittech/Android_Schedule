package com.example.timehelperserver.controller;

import com.example.timehelperserver.pojo.User;
import com.example.timehelperserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/login")
    public String login(@RequestBody User user) {
        return userService.loginService(user.getUsername(), user.getPassword());
    }

    @RequestMapping("/register")
    public String register(@RequestBody User user) {
        return userService.registerService(user);
    }

}

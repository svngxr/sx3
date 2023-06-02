package com.example.controller;

import com.example.entity.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    //通过ID获取用户
    @GetMapping("/users/{userId}")
    public User getUserById(@PathVariable int userId) {
        return userService.getById(userId);
    }

    //通过名字获取用户
    @GetMapping("/usersName/{userName}")
    public User getUserByName(@PathVariable String userName) {
        return userService.getByUserName(userName);
    }

    //通过状态获取用户
    @GetMapping("/users/type/{type}")
    public List<User> getUserByType(@PathVariable String type) {
        return userService.getUserByType(type);
    }

    //获取所有用户
    @GetMapping("/users/")
    public List<User> getAllUsers() {

        return userService.list();
    }

    //添加用户
    @PostMapping("/users/")
    public boolean addUser(@RequestBody User user) {

        User user1 = userService.getByUserName(user.getUserName());

        if (user1 != null) {
            return false;
        }

        return userService.save(user);
    }

    //用户登录
    @PostMapping("/users/login")
    public boolean loginUser(@RequestBody User user) {
        System.out.println("hi hi hi hi hi hi");
        System.out.println(user);
        User storedUser = userService.getByUserName(user.getUserName());
        return storedUser != null && storedUser.getPassword().equals(user.getPassword()) && storedUser.getType().equals(user.getType());
    }

    //更新用户
    @PutMapping("/users/")
    public boolean updateUser(@RequestBody User user) {
        return userService.updateById(user);
    }

    //删除用户
    @DeleteMapping("/users/{userId}")
    public boolean deleteUser(@PathVariable int userId) {
        return userService.removeById(userId);
    }

}

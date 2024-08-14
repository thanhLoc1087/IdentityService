package com.loc.identity_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loc.identity_service.dto.request.UserCreationRequest;
import com.loc.identity_service.dto.request.UserUpdateRequest;
import com.loc.identity_service.entity.User;
import com.loc.identity_service.service.UserService;

import jakarta.validation.Valid;






@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public User getMethodName(@PathVariable String userId) {
        return userService.getUser(userId);
    }
    
    
    @PostMapping
    public User createUser(@RequestBody @Valid UserCreationRequest request) {
        return userService.createUser(request);
    }    

    @PutMapping("/{userId}")
    public User putMethodName(@PathVariable String userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId);
        return "User has been deleted";
    }
}

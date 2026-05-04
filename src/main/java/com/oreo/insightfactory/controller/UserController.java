package com.oreo.insightfactory.controller;

import com.oreo.insightfactory.dto.UserResponse;
import com.oreo.insightfactory.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    List<UserResponse> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    UserResponse findById(@PathVariable String id) {
        return userService.findById(parseId(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable String id) {
        userService.delete(parseId(id));
    }

    private Long parseId(String id) {
        return Long.parseLong(id.startsWith("u_") ? id.substring(2) : id);
    }
}

package com.github.vspiewak.jbeatnik.controller;

import com.github.vspiewak.jbeatnik.domain.User;
import com.github.vspiewak.jbeatnik.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.List;

@RestController
public class UserController {

    @Inject
    private UserService userService;

    @RequestMapping(value="/user", method=RequestMethod.GET)
    public @ResponseBody
    List<User> allUsers() {
        return userService.findAll();
    }


    @RequestMapping(value = "/user", method=RequestMethod.POST)
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }

}

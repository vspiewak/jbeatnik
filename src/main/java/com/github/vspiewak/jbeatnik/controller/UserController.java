package com.github.vspiewak.jbeatnik.controller;

import com.github.vspiewak.jbeatnik.domain.User;
import com.github.vspiewak.jbeatnik.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.TokenService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Inject
    private UserService userService;

    @RequestMapping(value="/profile", method=RequestMethod.GET)
    public ResponseEntity<User> profile() {

        User currentUser = userService.getAuthenticatedUser();

        if(currentUser == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(currentUser, HttpStatus.OK);

    }

    @RequestMapping(value="/user", method=RequestMethod.GET)
    public @ResponseBody List<User> allUsers() {
        return userService.findAll();
    }

    @RequestMapping(value = "/user", method=RequestMethod.POST)
    public User createUser(@RequestBody User user) {
        return userService.save(user);
    }

}

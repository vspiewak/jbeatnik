package com.github.vspiewak.jbeatnik.controller;

import com.github.vspiewak.jbeatnik.domain.Authority;
import com.github.vspiewak.jbeatnik.domain.User;
import com.github.vspiewak.jbeatnik.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserController {

    @Inject
    private UserService userService;

    @RequestMapping(value = "/register",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody User user) {

        if (userService.alreadyExist(user.getUsername())) {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        } else {
            user = userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail().trim().toLowerCase());
            user.setPassword(null);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        }

    }

    @RequestMapping(value="/profile", method=RequestMethod.GET)
    public ResponseEntity<User> profile() {

        User currentUser = userService.getAuthenticatedUser();

        if(currentUser == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        currentUser.setPassword(null);

        return new ResponseEntity<>(currentUser, HttpStatus.OK);

    }

}

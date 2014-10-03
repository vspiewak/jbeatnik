package com.github.vspiewak.jbeatnik.controller;

import com.github.vspiewak.jbeatnik.domain.User;
import com.github.vspiewak.jbeatnik.service.MailService;
import com.github.vspiewak.jbeatnik.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.context.SpringWebContext;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Inject
    private SpringTemplateEngine templateEngine;

    @Inject
    private MailService mailService;

    @Inject
    private UserService userService;

    @RequestMapping(value = "/register",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {

        String usernamePattern = "^[a-zA-Z0-9]+$";
        boolean validUsername = user.getUsername().matches(usernamePattern);
        boolean usernameAlreadyExist = userService.alreadyExist(user.getUsername());
        boolean emailAlreadyExist = userService.emailAlreadyExist(user.getEmail());

        if(!validUsername) {
            return new ResponseEntity<>(new Error(4, "Username not valid, must match: " + usernamePattern), HttpStatus.BAD_REQUEST);
        } else if(usernameAlreadyExist && emailAlreadyExist) {
            return new ResponseEntity<>(new Error(3, "Username and Email already exist"), HttpStatus.BAD_REQUEST);
        } else if (emailAlreadyExist) {
            return new ResponseEntity<>(new Error(2, "Email already exist"), HttpStatus.BAD_REQUEST);
        } else if (usernameAlreadyExist) {
            return new ResponseEntity<>(new Error(1, "Username already exist"), HttpStatus.BAD_REQUEST);
        } else {

            user = userService.registerUser(user.getUsername(), user.getPassword(), user.getEmail().trim().toLowerCase());
            user.setPassword(null);

            //TODO: replace with user locale
            Locale locale = Locale.ENGLISH;
            String content = createHtmlContentFromTemplate("accountActivationEmail", user, locale, request, response);
            mailService.sendAccountActivationEmail(user.getEmail(), content, locale);
            return new ResponseEntity<>(user, HttpStatus.CREATED);

        }

    }

    @RequestMapping(value = "/lostpassword", method = RequestMethod.POST)
    public ResponseEntity<?> lostPassword(@RequestBody User user, HttpServletRequest request, HttpServletResponse response) {

        if (!StringUtils.isEmpty(user.getEmail())) {

            User resetUser = userService.lostPassword(user.getEmail().trim().toLowerCase());

            if (resetUser != null) {
                //TODO: replace with user locale
                Locale locale = Locale.ENGLISH;
                String content = createHtmlContentFromTemplate("resetPasswordEmail", resetUser, locale, request, response);
                mailService.sendResetPasswordEmail(resetUser.getEmail(), content, locale);
                return new ResponseEntity<>(HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value = "/activate", method = RequestMethod.GET)
    public ResponseEntity<?> activateUser(@RequestParam String email, @RequestParam String key) {

        if (!StringUtils.isEmpty(email) &&
                !StringUtils.isEmpty(key)) {

            User activatedUser = userService.activateUser(
                    email.trim().toLowerCase(),
                    key.trim().toLowerCase());

            if (activatedUser != null) {
                return new ResponseEntity<>(HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
    public ResponseEntity<?> resetPassword(@RequestBody User user) {

        if (!StringUtils.isEmpty(user.getEmail()) &&
                !StringUtils.isEmpty(user.getPassword()) &&
                !StringUtils.isEmpty(user.getResetPasswordKey())) {

            User resetUser = userService.resetPassword(
                    user.getEmail().trim().toLowerCase(),
                    user.getPassword(),
                    user.getResetPasswordKey().trim().toLowerCase());

            if (resetUser != null) {
                return new ResponseEntity<>(HttpStatus.OK);
            }

        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ResponseEntity<User> profile() {

        User currentUser = userService.getAuthenticatedUser();

        if (currentUser == null) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        currentUser.setPassword(null);

        return new ResponseEntity<>(currentUser, HttpStatus.OK);

    }

    private String createHtmlContentFromTemplate(final String template, final User user, final Locale locale, final HttpServletRequest request,
                                                 final HttpServletResponse response) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("user", user);
        variables.put("baseUrl", request.getScheme() + "://" +   // "http" + "://
                request.getServerName() +       // "myhost"
                ":" + request.getServerPort());
        IWebContext context = new SpringWebContext(request, response, servletContext,
                locale, variables, applicationContext);
        return templateEngine.process(template, context);
    }

}

package com.github.vspiewak.jbeatnik.service;

import com.github.vspiewak.jbeatnik.domain.Authority;
import com.github.vspiewak.jbeatnik.domain.User;
import com.github.vspiewak.jbeatnik.repository.AuthorityRepository;
import com.github.vspiewak.jbeatnik.repository.UserRepository;
import com.github.vspiewak.jbeatnik.utils.SecurityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.*;

@Service
@Transactional
public class UserService {

    @Inject
    private PasswordEncoder passwordEncoder;

    @Inject
    private AuthorityRepository authorityRepository;

    @Inject
    private UserRepository userRepository;

    //TODO: remove
    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public boolean alreadyExist(String username) {
        return userRepository.findByUsernameCaseInsensitive(username) != null;
    }

    @Transactional(readOnly = true)
    public boolean emailAlreadyExist(String email) {
        return userRepository.findByEmail(email) != null;
    }

    @Transactional
    public User registerUser(String username, String password, String email) {
        User user = new User();
        Authority authority = authorityRepository.findOne("ROLE_USER");
        Set<Authority> authorities = new HashSet<Authority>();
        String encryptedPassword = passwordEncoder.encode(password);
        user.setUsername(username);
        user.setPassword(encryptedPassword);
        user.setEmail(email);
        user.setActivated(false);
        String key = UUID.randomUUID().toString();
        user.setActivationKey(key);
        authorities.add(authority);
        user.setAuthorities(authorities);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getAuthenticatedUser() {
        String currentUsername = SecurityUtils.getCurrentUsername();
        User user = null;
        if(currentUsername != null) {
            user = userRepository.findOne(currentUsername);
            user.getAuthorities().size(); // eagerly load user/authorities association
        }
        return user;
    }

    @Transactional
    public User lostPassword(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            String key = UUID.randomUUID().toString();
            user.setResetPasswordKey(key);
            user = userRepository.save(user);
        }
        return user;
    }

    @Transactional
    public User activateUser(String email, String activationKey) {
        User user = userRepository.findByEmailAndActivationKey(email, activationKey);
        if (user != null) {
            user.setActivated(true);
            user.setActivationKey(null);
            user = userRepository.save(user);
        }
        return user;
    }

    @Transactional
    public User resetPassword(String email, String password, String resetPasswordKey) {
        User user = userRepository.findByEmailAndResetPasswordKey(email, resetPasswordKey);
        if (user != null) {
            String encryptedPassword = passwordEncoder.encode(password);
            user.setPassword(encryptedPassword);
            user.setResetPasswordKey(null);
            user = userRepository.save(user);
        }
        return user;
    }

}

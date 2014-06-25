package com.github.vspiewak.jbeatnik.service;

import com.github.vspiewak.jbeatnik.domain.User;
import com.github.vspiewak.jbeatnik.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Service
@Transactional
public class UserService {

    @Inject
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

}

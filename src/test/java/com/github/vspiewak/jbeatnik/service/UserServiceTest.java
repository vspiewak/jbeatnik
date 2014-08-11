package com.github.vspiewak.jbeatnik.service;

import com.github.vspiewak.jbeatnik.Application;
import com.github.vspiewak.jbeatnik.domain.User;
import com.github.vspiewak.jbeatnik.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class UserServiceTest {

    @Inject
    private UserRepository userRepository;

    @Inject
    private UserService userService;

    @Test
    public void should_find_the_user_after_a_save() {

        User user = new User();
        user.setUsername("toto");
        user.setPassword("toto");
        user.setEmail("toto@mail.me");

        User savedUser = userService.save(user);

        List<User> users = userRepository.findAll();
        assertThat(users, is(not(empty())));
        assertThat(users, hasItem(savedUser));

    }

}
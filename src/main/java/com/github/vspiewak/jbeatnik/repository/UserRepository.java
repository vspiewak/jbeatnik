package com.github.vspiewak.jbeatnik.repository;

import com.github.vspiewak.jbeatnik.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, String> {

    @Query
    User findByEmail(String email);

    @Query
    User findByEmailAndResetPasswordKey(String email, String resetPasswordKey);

}
package com.github.vspiewak.jbeatnik.repository;

import com.github.vspiewak.jbeatnik.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
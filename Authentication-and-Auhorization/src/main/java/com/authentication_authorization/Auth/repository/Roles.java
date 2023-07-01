package com.authentication_authorization.Auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authentication_authorization.Auth.model.ERole;
import com.authentication_authorization.Auth.model.Role;

public interface Roles extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);
    
}

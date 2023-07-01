package com.authentication_authorization.Auth.security.service;

import com.authentication_authorization.Auth.model.User;
import com.authentication_authorization.Auth.repository.UsersInterface;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetialsServiceImpl implements UserDetailsService {
    @Autowired
    UsersInterface userRepository;
    
    @Override
    @Transactional
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userRepository.findByUsername(username)
             .orElseThrow(()-> new UsernameNotFoundException("User not found with username "+ username));
          return UserDetialsImpl.build(user);
        }
    
    
    
}

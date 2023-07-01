package com.authentication_authorization.Auth.controller;

import com.authentication_authorization.Auth.model.ERole;
import com.authentication_authorization.Auth.model.Role;
import com.authentication_authorization.Auth.model.User;
import com.authentication_authorization.Auth.payload.request.LoginRequest;
import com.authentication_authorization.Auth.payload.request.SignupRequest;
import com.authentication_authorization.Auth.payload.response.JwtResponse;
import com.authentication_authorization.Auth.payload.response.MessageResponse;
import com.authentication_authorization.Auth.repository.Roles;
import com.authentication_authorization.Auth.repository.UsersInterface;
import com.authentication_authorization.Auth.security.jwt.JwtUtils;
import com.authentication_authorization.Auth.security.service.UserDetialsImpl;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*" , maxAge=3600) //allow all 
@RestController
@RequestMapping("/v1/api/auth")
public class AuthController {
    
    @Autowired
    UsersInterface userRepository;
    
    @Autowired
    Roles roles;
    
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;



    @ApiResponses(value={
        @ApiResponse(responseCode="200", content=@Content),
        @ApiResponse(responseCode="400", content=@Content)
    })
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if(userRepository.existsByUsername(signupRequest.getUsername())){
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Error: Username is already Taken!"));
                        }
        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Email is alread in use!"));
        }
        
        //create new User Account
        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), encoder.encode(signupRequest.getPassword()));
        Set<String> StRole = signupRequest.getRoles();
        Set<Role> role = new HashSet<>();
        if(StRole==null){
            Role userRole = roles.findByName(ERole.ROLE_USER)
                    .orElseThrow(()-> new RuntimeException("Error: Role not Found"));
            role.add(userRole);
        }else{
            StRole.forEach((var role_)->{
                switch(role_){
                    case "admin" -> {
                        Role admin_role = roles.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(()-> new RuntimeException("Error: Role not Found"));
                        role.add(admin_role);
                    }
                        
                    case "moderator" -> {
                        Role moderator_role = roles.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(()-> new RuntimeException("Error: Role not Found"));
                        role.add(moderator_role);
                    }
                        
                    default -> {
                        Role user_role = roles.findByName(ERole.ROLE_USER)
                                .orElseThrow(()-> new RuntimeException("Error: Role not Found"));
                        role.add(user_role);
                    }
                        
                }
            });
           
        }
            user.setRoles(role);
            userRepository.save(user);
            return ResponseEntity.ok(new MessageResponse("User registered Succesfully!"));
        
    }
   
    
    //Sign-in
      @ApiResponses(value={
        @ApiResponse(responseCode="200", content=@Content),
        @ApiResponse(responseCode="500", content=@Content)
    })
    @PostMapping("sign-in")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetialsImpl userDetails = (UserDetialsImpl)authentication.getPrincipal(); //get Currently logged in user
            List<String> roles = userDetails.getAuthorities().stream()
            .map(item-> item.getAuthority())
            .collect(Collectors.toList());
            return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));



    }
    
}

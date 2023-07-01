package com.authentication_authorization.Auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.authentication_authorization.Auth.model.User;
import com.authentication_authorization.Auth.payload.response.MessageResponse;
import com.authentication_authorization.Auth.repository.Roles;
import com.authentication_authorization.Auth.repository.UsersInterface;
import com.authentication_authorization.Auth.security.jwt.JwtUtils;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
@CrossOrigin(origins = "*" , maxAge=3600)
@RestController
@RequestMapping("/v1/api/role/")
@SecurityRequirement(name="bearerAuth")
public class RoleController {

    @Autowired
    UsersInterface userRepository;

    @Autowired
    Roles roleRepository;
        
    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwUtils;

    @ApiResponses(value={
        @ApiResponse(responseCode="200", content=@Content)
    })
    @GetMapping("/all")
    public String allAcess(){
        return "public Content";
    }


     @ApiResponses(value={
        @ApiResponse(responseCode="200", content=@Content),
        @ApiResponse(responseCode="401", content=@Content)
    })
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAcess(){
        return "User Board";
    }

    @ApiResponses(value={
        @ApiResponse(responseCode="200", content=@Content),
        @ApiResponse(responseCode="401", content=@Content)
    })
    @GetMapping("/moderator")
    @PreAuthorize("hasRole('MODERATOR')")
    public String moderatorAcess(){
        return "Moderator Board";
    }

    @ApiResponses(value={
        @ApiResponse(responseCode="200", content=@Content),
        @ApiResponse(responseCode="401", content=@Content)
    })
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAcess(){
        return "Admin Board";
    }

  @GetMapping("/profile")
  public ResponseEntity<?> userProfile(Authentication auth){
    String username = auth.getName();
    User user = userRepository.findByUsername(username)
    .orElseThrow(()-> new RuntimeException("User not found"));
         return ResponseEntity.ok(user);
  
    
  }

    // @PutMapping("user/update-profile")
    // public ResponseEntity<?> updateProfile( @RequestBody User user, Authentication auth){
    //   String username = auth.getName();
    //   User findbyId =  userRepository.findByUsername(username)
    //   .orElseThrow(()-> new RuntimeException("No Id found"));
    //   findbyId.setId(user.getId());
    //   findbyId.setUsername(user.getUsername());
    //   findbyId.setEmail(user.getEmail());
    //   findbyId.setRoles(user.getRoles());
    //   findbyId.setPassword(encoder.encode(findbyId.getPassword()));
    // //    userRepository.save(user);
    //    return ResponseEntity.ok( userRepository.save(user));
        
    // }


    @ApiResponses(value={
        @ApiResponse(responseCode="200", content=@Content),
        @ApiResponse(responseCode="204", content=@Content)
    })
    @DeleteMapping("admin/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUserbyId(@PathVariable("id")Long id){
        userRepository.deleteById(id);
        return ResponseEntity.ok(new MessageResponse("user Succesully deleted"));

    }

    
}

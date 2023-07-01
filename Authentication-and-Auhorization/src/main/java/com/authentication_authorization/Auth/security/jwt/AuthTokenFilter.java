package com.authentication_authorization.Auth.security.jwt;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.authentication_authorization.Auth.security.service.UserDetialsServiceImpl;

public class AuthTokenFilter extends OncePerRequestFilter{
    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private  UserDetialsServiceImpl userDetailsService;
    
    private static final Logger loger  = LoggerFactory.getLogger(AuthTokenFilter.class);
    
    /**
     *
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{
        try{
            String jwt  = parseJwt(request);
            if(jwt!=null && jwtUtils.validateJwtToken(jwt)){
                String username = jwtUtils.getUsernameFromToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken authentication = new   UsernamePasswordAuthenticationToken (
                                userDetails,null, userDetails.getAuthorities()
                        );
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication); //where we store the details of the Authenticated user
            }
        }catch(UsernameNotFoundException e){
            loger.error("can not set user Authentication: {}", e);
        }
        filterChain.doFilter(request, response);
    }
    
    private String  parseJwt(HttpServletRequest request){ //headerAuth 
        String headAuth = request.getHeader("Authorization");
        if(StringUtils.hasText(headAuth) && headAuth.startsWith("Bearer")){
            return headAuth.substring(7);
        }
        
        return null;
        
    }
    
}

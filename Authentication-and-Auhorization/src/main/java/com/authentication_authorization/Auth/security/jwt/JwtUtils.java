package com.authentication_authorization.Auth.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

import java.security.Key;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.authentication_authorization.Auth.security.service.UserDetialsImpl;

@Component
public class JwtUtils {
    private static final Logger loger = LoggerFactory.getLogger(JwtUtils.class);
    
//    @Value("${Auth.app.jwtSecret}")
    private String jwtSecret = "======================Auth=Spring====================";
//    
//    @Value("${Auth.app.jwtExpiration }")
    private int jwtExpirationMs=86400000;
    
    public String generateJwtToken(Authentication authentication){
         UserDetialsImpl useprincipal = (UserDetialsImpl)authentication.getPrincipal();
         return Jwts.builder()
                 .setSubject(useprincipal.getUsername())
                 .setIssuedAt(new Date())
                 .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                 .signWith(key(), SignatureAlgorithm.HS256)
                 .compact();
                 
    }
    
    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
    
    public String generateTokenFromUsername(String username){
        return Jwts.builder().setSubject(username).setIssuedAt(new Date())
        .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)).signWith(key(), SignatureAlgorithm.HS256)
        .compact();

    }
    public String getUsernameFromToken(String token){
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
    
    //validating the Authentication Token
    public Boolean validateJwtToken(String authToken){
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        }catch(SignatureException e){
            loger.error("Invalid JWT Signature {}" + e.getMessage());
        }catch(MalformedJwtException e){
            loger.error("Invalid JWT token {} " +e.getMessage());
        }catch(ExpiredJwtException e){
            loger.error("JWT token is Expired {} " + e.getMessage());
        }catch(UnsupportedJwtException e){
            loger.error("JWT token is unsupported {} " + e.getMessage());
        }catch(IllegalArgumentException e){
            loger.error("JWT claims String is Empty {} "+ e.getMessage());
        }
        return false;
    }
    
    
}

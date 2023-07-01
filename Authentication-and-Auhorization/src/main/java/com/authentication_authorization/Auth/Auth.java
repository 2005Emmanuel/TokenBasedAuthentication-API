package com.authentication_authorization.Auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@OpenAPIDefinition(info=@Info(title="TokenBasedAuthentication API", version="v1.0.0", description="This a TokenBasedAuthentication API ."))
@SecurityScheme(name="bearerAuth", type =SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class Auth {

	public static void main(String[] args) {
		SpringApplication.run(Auth.class, args);
	}

}

package com.sintropia.calculator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.sintropia.calculator.filter.JwtFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

	private final JwtFilter jwtFilter;

	public SecurityConfig(JwtFilter jwtFilter){
		this.jwtFilter = jwtFilter;
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/api/auth/validate",
					"/api/auth/login",
					"/api/user/register",
					"/register",
					"/login",
					"/error/**",
					"/css/**",
					"/js/**",
					"/images/**"
				).permitAll()
				.anyRequest().authenticated()
			)
			.exceptionHandling(ex -> ex
		            .authenticationEntryPoint((request, response, authException) -> {
		                response.sendRedirect("/login");
		            })
		    )
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

}

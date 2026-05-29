package com.sintropia.calculator.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.sintropia.calculator.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	public JwtFilter(JwtService jwtService){
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain) throws ServletException, IOException {

		String token = null;
		
		if(request.getCookies() != null) {
			for(Cookie cookie : request.getCookies()) {
				if("AUTH_TOKEN".equals(cookie.getName())) {
					token = cookie.getValue();
					break;
				}	
			}
		}
		
		if(token == null){
			SecurityContextHolder.getContext().setAuthentication(
					new AnonymousAuthenticationToken(
							"anonymous",
							"anonymousUser",
							List.of(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
					)
			);
	        chain.doFilter(request, response);
	        return;
	    }

		String email = jwtService.extractEmail(token);
		
		if(email != null && SecurityContextHolder.getContext().getAuthentication() == null){
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
			
		chain.doFilter(request, response);
	}
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
	    String path = request.getRequestURI();
	    
	    return path.equals("/api/auth/login") || 
	           path.equals("/api/auth/register") || 
	           path.equals("/api/auth/validate") ||
	           path.equals("/login") ||
	           path.equals("/register");
	}
}

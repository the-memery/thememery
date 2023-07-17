package com.quest.etna.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quest.etna.model.JwtUserDetails;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.quest.etna.model.User;
import com.quest.etna.repositories.UserRepository;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	private final UserRepository userRepository;
	private final JwtTokenUtil jwtTokenUtil;
	private final JwtUserDetails jwtUserDetails;

	public JwtRequestFilter(JwtTokenUtil jwtTokenUtil, UserRepository userRepo, JwtUserDetails jwtUserDetails) {
		this.jwtTokenUtil = jwtTokenUtil;
		this.userRepository = userRepo;
		this.jwtUserDetails = jwtUserDetails;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws AuthenticationException, ServletException, IOException {
		if ("/testSuccess".equals(request.getRequestURI())
				|| "/register".equals(request.getRequestURI())
				|| "/authenticate".equals(request.getRequestURI())) {
			chain.doFilter(request, response);
			return;
		}

		final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header == null) {
			chain.doFilter(request, response);
			return;
		}

		final String token = header.split(" ")[1].trim();
		final String username;

		try {
			username = jwtTokenUtil.getUsernameFromToken(token);
		} catch (ExpiredJwtException e) {
			final String expiredMsg = e.getMessage();
			logger.warn(expiredMsg);

			final String msg = (expiredMsg != null) ? expiredMsg : "Unauthorized";
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
			return;
		}

		User user = userRepository.getByUsername(username);
		if (user != null) {
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					user.getUsername(),
					user.getPassword(), jwtUserDetails.getAuthorities());

			SecurityContextHolder.getContext().setAuthentication(authentication);
			chain.doFilter(request, response);
		} else {
			response.setContentType("application/json");
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			//response.sendError(Unauthorized);
			response.getOutputStream().println("{ \"error\": \"You modified your username. Please authenticate again.\" }");
		}
	}

}

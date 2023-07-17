package com.quest.etna.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	private JwtAuthenticationEntryPoint entryPoint;
	private JwtUserDetailsService userDetailsService;
	private JwtRequestFilter jwtTokenFilter;

	public WebSecurityConfig(JwtAuthenticationEntryPoint entryPoint, JwtUserDetailsService userDetailsService,
			JwtRequestFilter jwtTokenFilter) {
		this.entryPoint = entryPoint;
		this.userDetailsService = userDetailsService;
		this.jwtTokenFilter = jwtTokenFilter;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Set permissions on endpoints
		http.cors();
		http.httpBasic().authenticationEntryPoint(entryPoint).and().authorizeRequests()
				// Our public endpoints
				.antMatchers(HttpMethod.GET, "/testSuccess").permitAll()
				.antMatchers(HttpMethod.POST, "/register").permitAll()
				.antMatchers(HttpMethod.POST, "/authenticate").permitAll()
				.antMatchers(HttpMethod.GET, "/user").permitAll()
				.antMatchers(HttpMethod.GET, "/event").permitAll()
				.antMatchers(HttpMethod.GET, "/event/*").permitAll()
				.antMatchers(HttpMethod.GET, "/artwork").permitAll()
				.antMatchers(HttpMethod.GET, "/e-commerce").permitAll()
				.antMatchers(HttpMethod.GET, "/artwork/artist/*").permitAll()
				.anyRequest().authenticated().and()
				.csrf().disable();

		http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}
}

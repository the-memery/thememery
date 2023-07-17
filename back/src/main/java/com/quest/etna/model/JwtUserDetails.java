package com.quest.etna.model;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Component
public class JwtUserDetails implements UserDetails {
	
	private static final long serialVersionUID = -8306189531357070962L;
	private User user;
	
	public JwtUserDetails(User user) {
		this.user = user;
	}

	//Added empty constructor
	public JwtUserDetails() {};

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		HashSet<GrantedAuthority> authorities = new HashSet<GrantedAuthority>(2);
		authorities.add(new SimpleGrantedAuthority(UserRole.ROLE_USER.toString()));
		authorities.add(new SimpleGrantedAuthority(UserRole.ROLE_ARTIST.toString()));
		authorities.add(new SimpleGrantedAuthority(UserRole.ROLE_ADMIN.toString()));
		return authorities;
	}
	
	@Override
	public String getUsername() {
		return this.user.getUsername();
	}

	@Override
	public String getPassword() {
		return this.user.getPassword();
	}
	
	public UserRole getRole() {
		return this.user.getRole();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}

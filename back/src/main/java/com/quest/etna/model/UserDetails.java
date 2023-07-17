package com.quest.etna.model;

import java.util.Objects;

public class UserDetails {
    private String username;
    private UserRole role;

    public UserDetails() {};

    public UserDetails(String username, UserRole role) {
        super();
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    @Override
	public String toString() {
		return "UserDetails [" + username + ", " + role + "]";
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(role, username);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDetails other = (UserDetails) obj;
		return role == other.role && Objects.equals(username, other.username);
	}
}

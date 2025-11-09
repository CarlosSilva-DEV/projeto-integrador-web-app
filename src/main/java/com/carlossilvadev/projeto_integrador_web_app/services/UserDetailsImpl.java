package com.carlossilvadev.projeto_integrador_web_app.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.UserRole;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String name;
	private String username;
	private String email;
	private String password;
	private UserRole role;
	private Collection<? extends GrantedAuthority> authorities;
	
	// Construtor
	public UserDetailsImpl(Long id, String name, String username, String password, String email,
			Collection<? extends GrantedAuthority> authorities, UserRole role) {
		super();
		this.id = id;
		this.name = name;
		this.username = username;
		this.password = password;
		this.email = email;
		this.authorities = authorities;
		this.role = role;
	}
	
	public static UserDetailsImpl build(User user) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		if (user.getRole() == UserRole.ROLE_ADMIN) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		} else {
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		}
		
		return new UserDetailsImpl(
				user.getId(), 
				user.getNome(),
				user.getLogin(),
				user.getSenha(),
				user.getEmail(),
				authorities,
				user.getRole()
				);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
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
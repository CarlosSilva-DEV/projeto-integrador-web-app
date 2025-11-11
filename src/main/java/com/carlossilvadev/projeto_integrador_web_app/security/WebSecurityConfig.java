package com.carlossilvadev.projeto_integrador_web_app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.carlossilvadev.projeto_integrador_web_app.security.jwt.AuthEntryPoint;
import com.carlossilvadev.projeto_integrador_web_app.security.jwt.AuthFilterToken;


@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class WebSecurityConfig {
	
	@Autowired
	private AuthEntryPoint unauthorizedHandler;
	
	@Autowired
	private AuthFilterToken authFilterToken;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults());
		http.csrf(csrf -> csrf.disable())
		.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(auth -> auth
				.requestMatchers("/auth/login", "/auth/register").permitAll()
				.requestMatchers("/users/profile").authenticated()
				.requestMatchers("/users/**").hasRole("ADMIN")
				.requestMatchers("/products/**").authenticated()
				.requestMatchers("/orders/**").authenticated()
				.requestMatchers("/categories/**").authenticated()
				.requestMatchers("/h2-console").permitAll()
				.anyRequest().authenticated());
		
		http.addFilterBefore(authFilterToken, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
}
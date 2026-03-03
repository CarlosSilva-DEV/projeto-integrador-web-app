package com.carlossilvadev.projeto_integrador_web_app.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.carlossilvadev.projeto_integrador_web_app.security.jwt.AuthEntryPoint;
import com.carlossilvadev.projeto_integrador_web_app.security.jwt.AuthFilterToken;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;


@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
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
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(List.of("*"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));
		http.csrf(csrf -> csrf.disable())
		.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests(auth -> auth
				// arquivos estáticos (html, css e js)
				.requestMatchers("/css/**", "/js/**", "/images/**", "/**.html", "/**.css", 
						"/**.js", "/**.png", "/**.jpg", "/**.ico", "/favicon.ico").permitAll()
				
				.requestMatchers("/", "/index.html", "/homepage.html",
                        "/login.html", "/cadastro.html", "/carrinho.html",
                        "/perfil.html", "/produtos.html", 
                        "/pagamento.html", "/pedido.html").permitAll()
				
				// endpoints públicos
				.requestMatchers("/auth/login", "/auth/register", "/products/**", "/categories/**").permitAll()
				
				// endpoints protegidos
				.requestMatchers("/users/profile/**").authenticated()
				
				.requestMatchers("/users/**", "/orders/**", "/payments/**").hasRole("ADMIN")
				
				// edpoints API Swagger
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

				.anyRequest().authenticated());
		
		http.addFilterBefore(authFilterToken, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
}
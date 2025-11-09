package com.carlossilvadev.projeto_integrador_web_app.security.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.carlossilvadev.projeto_integrador_web_app.services.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	
	@Value("${projeto.jwtSecret}")
	private String jwtSecret;
	
	@Value("${projeto.jwtExpirationMs}")
	private int jwtExpirationMs;

    public String generateTokenFromUserDetailsImpl(UserDetailsImpl userDetails) {
    	return Jwts.builder().subject(userDetails.getUsername())
    			.issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
    			.signWith(getSigningKey()).compact();
    }
	
	private SecretKey getSigningKey() {
		String key = jwtSecret;
		return Keys.hmacShaKeyFor(key.getBytes());
	}
	
	public String getUsernameToken(String token) {
		return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload().getSubject();
	}
	
	public boolean validateJwtToken(String authToken) {
		try {
			Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(authToken);
			return true;
		} catch (MalformedJwtException exception) {
			System.out.println("Token inválido " + exception.getMessage());
		} catch (ExpiredJwtException exception) {
			System.out.println("Token expirado " + exception.getMessage());
		} catch (UnsupportedJwtException exception) {
			System.out.println("Token não suportado " + exception.getMessage());
		} catch (IllegalArgumentException exception) {
			System.out.println("Argumento inválido " + exception.getMessage());
		} catch (SecurityException exception) {
			System.out.println("Assinatura inválida " + exception.getMessage());
		}
		return false;
	}
}
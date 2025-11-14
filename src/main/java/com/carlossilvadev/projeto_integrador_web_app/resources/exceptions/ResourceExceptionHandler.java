package com.carlossilvadev.projeto_integrador_web_app.resources.exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.BusinessException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.DatabaseException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.InvalidCredentialsException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.ResourceNotFoundException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.UserNotAuthenticatedException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice // intercepta as exceções para realizar o tratamento
public class ResourceExceptionHandler {
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException exception, HttpServletRequest request) {
		String error = "Resource not found";
		HttpStatus status = HttpStatus.NOT_FOUND;
		StandardError err = new StandardError(Instant.now(), status.value(), error, exception.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(DatabaseException.class)
	public ResponseEntity<StandardError> database(DatabaseException exception, HttpServletRequest request) {
		String error = "Database error";
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardError err = new StandardError(Instant.now(), status.value(), error, exception.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<StandardError> invalidCredentials(InvalidCredentialsException exception, HttpServletRequest request) {
		String error = "Invalid credentials";
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		StandardError err = new StandardError(Instant.now(), status.value(), error, exception.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<StandardError> BusinessException(BusinessException exception, HttpServletRequest request) {
		String error = "Business rule violation";
		HttpStatus status = HttpStatus.CONFLICT;
		StandardError err = new StandardError(Instant.now(), status.value(), error, exception.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<StandardError> handleValidationException(MethodArgumentNotValidException exception, HttpServletRequest request) {
		String error = "Invalid input data";
		HttpStatus status = HttpStatus.BAD_REQUEST;
		StandardError err = new StandardError(Instant.now(), status.value(), error, exception.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(UserNotAuthenticatedException.class)
	public ResponseEntity<StandardError> handleUserNotAuthenticated(UserNotAuthenticatedException exception, HttpServletRequest request) {
		String error = "User not authenticated";
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		StandardError err = new StandardError(Instant.now(), status.value(), error, exception.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
	
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<StandardError> handleUserNotFound(UserNotFoundException exception, HttpServletRequest request) {
		String error = "User not found";
		HttpStatus status = HttpStatus.NOT_FOUND;
		StandardError err = new StandardError(Instant.now(), status.value(), error, exception.getMessage(), request.getRequestURI());
		return ResponseEntity.status(status).body(err);
	}
}
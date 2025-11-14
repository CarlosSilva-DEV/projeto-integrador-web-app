package com.carlossilvadev.projeto_integrador_web_app.services.exceptions;

public class UserNotAuthenticatedException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public UserNotAuthenticatedException(String msg) {
		super(msg);
	}
}
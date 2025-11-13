package com.carlossilvadev.projeto_integrador_web_app.services.exceptions;

public class InvalidCredentialsException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	
	public InvalidCredentialsException(String msg) {
		super(msg);
	}
}
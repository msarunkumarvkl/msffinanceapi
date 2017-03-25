package com.innomind.msffinance.web.exceptions;

public class ValidationException extends RuntimeException {
	public ValidationException(String message){
		super(message);
	}
}

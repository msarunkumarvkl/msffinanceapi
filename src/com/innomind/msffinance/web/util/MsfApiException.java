package com.innomind.msffinance.web.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.innomind.msffinance.web.exceptions.*;
@ControllerAdvice
public class MsfApiException {
	
	/*
	 * Response Code 409
	 */
	@ExceptionHandler(ObjectExistsException.class)
	public ResponseEntity<String> throwObjectExistsException(Exception e){
		return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
	}
	
	/*
	 * Response Code 404
	 */
	@ExceptionHandler(ObjectNotExistsException.class)
	public ResponseEntity<String> throwObjectNotExistsException(Exception e){
		return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
	}
	
	/*
	 * Response Code 417
	 */
	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<String> throwValidationException(Exception e){
		return new ResponseEntity<>(e.getMessage(),HttpStatus.EXPECTATION_FAILED);
	}
	
	/*
	 * Response Code 500
	 */
	@ExceptionHandler(MsfFinanceException.class)
	public ResponseEntity<String> throwMsfFinanceException(Exception e){
		return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
	}
}

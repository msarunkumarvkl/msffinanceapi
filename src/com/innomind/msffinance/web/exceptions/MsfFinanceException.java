package com.innomind.msffinance.web.exceptions;

public class MsfFinanceException extends RuntimeException{
	
	public MsfFinanceException(){
		super("Sorry for the inconvenience!!!");		
	}
	
	public MsfFinanceException(String message){
		super(message);		
	}
}

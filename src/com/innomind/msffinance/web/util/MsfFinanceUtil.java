package com.innomind.msffinance.web.util;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.innomind.msffinance.web.exceptions.MsfFinanceException;
import com.innomind.msffinance.web.exceptions.ValidationException;

@Component
public class MsfFinanceUtil {
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final String NUMBER_PATTERN = "\\d+";
	
	public Date getTodayDate(){
		Date date = new Date();
		String formattedDate = formatDate(date);
        return parseDate(formattedDate);
	}
    
	public Date parseDate(String date){
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
        	throw new ValidationException("Invalid Date Format!");
        }
    }
	
    public String formatDate(Date date){
        return dateFormat.format(date);
    }

    public boolean isNotNull(String nullValue) {
        if(nullValue != null && nullValue != "" && !nullValue.isEmpty()){
            return true;
        }
        return false;
    }
    
    public boolean isNumber(String number){
    	Pattern pattern = Pattern.compile(NUMBER_PATTERN);
    	Matcher match = pattern.matcher(number);
    	return match.matches();
    }

	public Date parseUrlDate(String dateString) throws ValidationException{
		SimpleDateFormat urlDateFormat = new SimpleDateFormat("ddMMyyyy");
        try {
            return urlDateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new ValidationException("Invalid Date Format!");
        }
	}
	
	public Object merge(Object obj, Object update) throws MsfFinanceException{
	    if(!obj.getClass().isAssignableFrom(update.getClass())){
	        return obj;
	    }

	    Method[] methods = obj.getClass().getMethods();

	    for(Method fromMethod: methods){
	        if(fromMethod.getDeclaringClass().equals(obj.getClass())
	                && fromMethod.getName().startsWith("get")){

	            String fromName = fromMethod.getName();
	            String toName = fromName.replace("get", "set");	            
	            try {
	            	if(!"getClientPhoto".equals(fromName)) {
	            		Method toMetod = obj.getClass().getMethod(toName, fromMethod.getReturnType());
		                Object value = fromMethod.invoke(update, (Object[])null);
		                if ((value instanceof String || value instanceof Set)
		                		|| (value instanceof Integer && (Integer)value != 0)) {
		                	Object fromValue = fromMethod.invoke(obj, (Object[])null);
		                	if ((fromValue instanceof String || fromValue instanceof Set)
			                		|| (fromValue instanceof Integer && (Integer)fromValue != 0)) {
		                		if(!value.equals(fromValue)) {
		                			toMetod.invoke(obj, value);
		                		}
		                	} else {
		                		if(!toName.equals("setInterestAmount")) {
	                				toMetod.invoke(obj, value);	
	                			}
		                	}
		                }	
	            	}
	            } catch (Exception e) {
	               throw new MsfFinanceException(e.getMessage());
	            } 
	        }
	    }
	    return obj;
	}
}

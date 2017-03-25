package com.innomind.msffinance.web.util;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Aspect
public class MsfApiAspect {
 private final Logger log = Logger.getLogger(this.getClass());
 
 @Around("execution(* com.innomind.farmerconnect.controller.*.*(..))")
 public Object logTimeMethod(ProceedingJoinPoint joinPoint)throws Throwable{
	 StopWatch stopwatch = new StopWatch();
	 stopwatch.start();
	 StringBuffer logMessage = new StringBuffer();
	 Object retVal = null;
	 boolean  errorExists= false;
	 Exception outException = null ;
	 try{
		 logMessage.append("Starting to execute Controller :"+joinPoint.getTarget().getClass().getName());
		 retVal = joinPoint.proceed();
	 }catch(Exception e){		 
		 logMessage.append("Exception Occured :"+e);
		 errorExists = true;
		 outException = e;
	 }
	 stopwatch.stop();
	 
	 logMessage.append("\n"+joinPoint.getSignature().getName());
	 logMessage.append("\t\t Execution Time : ");
	 logMessage.append(stopwatch.getTotalTimeMillis() + " ms");
	 logMessage.append("\n Ending the execution of Controller :"+joinPoint.getTarget().getClass().getName());
	 if(!errorExists){
		 log.info(logMessage);
	 }else{
		 log.error(logMessage); 
		 throw outException;
	 }
	 
	 return retVal;
	 
 }
}

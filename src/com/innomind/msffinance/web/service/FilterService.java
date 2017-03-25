package com.innomind.msffinance.web.service;

import java.util.List;
import java.util.Map;

import com.innomind.msffinance.web.exceptions.MsfFinanceException;

public interface FilterService {

	List<Object> getTenureTypes() throws MsfFinanceException;

	List<Object> getDocumentTypes() throws MsfFinanceException;

	Map<String, List<Object>> getFilters()throws MsfFinanceException;
	
	List<Object> getInterestTypes() throws MsfFinanceException;
	
	List<Object> getStatusTypes() throws MsfFinanceException;

}

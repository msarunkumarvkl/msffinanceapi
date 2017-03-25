package com.innomind.msffinance.web.dao;

import java.util.List;

import com.innomind.msffinance.web.exceptions.MsfFinanceException;
import com.innomind.msffinance.web.model.MsfStatus;
import com.innomind.msffinance.web.model.MsfTenureType;

public interface FilterDao {

	List<Object> getTenureTypes() throws MsfFinanceException;

	List<Object> getDocumentTypes() throws MsfFinanceException;

	List<Object> getInterestTypes()throws MsfFinanceException;

	MsfStatus getMsfStatus(String string);

	List<Object> getFilterTypes();

	MsfTenureType getTenureType(int tenureTypeId);

	MsfStatus getMsfStatus(int statusId);

}

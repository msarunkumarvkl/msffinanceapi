package com.innomind.msffinance.web.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.innomind.msffinance.web.dao.FilterDao;
import com.innomind.msffinance.web.exceptions.MsfFinanceException;

@Service("filterService")
@Transactional(propagation = Propagation.REQUIRED)
public class FilterServiceImpl implements FilterService{
	@Autowired
	FilterDao filterDao;

	@Override
	public List<Object> getTenureTypes() throws MsfFinanceException {
		try{			
			return filterDao.getTenureTypes();
		}catch(Exception e){
			throw e;
		}
	}

	@Override
	public List<Object> getDocumentTypes() throws MsfFinanceException {
		try{
			return filterDao.getDocumentTypes();
		}catch(Exception e){
			throw e;
		}
	}

	@Override
	public Map<String, List<Object>> getFilters() throws MsfFinanceException {
		Map<String,List<Object>> returnList = new HashMap<>();
		List<Object> dataList = getTenureTypes();
		returnList.put("tenure", dataList);
		dataList = getDocumentTypes();
		returnList.put("document", dataList);
		dataList = getInterestTypes();
		returnList.put("interest", dataList);
		dataList = getStatusTypes();
		returnList.put("status", dataList);
		return returnList;
	}
	
	@Override
	public List<Object> getStatusTypes() throws MsfFinanceException {
		try{
			return filterDao.getFilterTypes();
		}catch(Exception e){
			throw e;
		}
	}

	@Override
	public List<Object> getInterestTypes()throws MsfFinanceException {
		try{
			return filterDao.getInterestTypes();
		}catch(MsfFinanceException e){
			throw e;
		}
	}
}

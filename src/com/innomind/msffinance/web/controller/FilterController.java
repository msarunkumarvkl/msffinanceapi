package com.innomind.msffinance.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.innomind.msffinance.web.exceptions.MsfFinanceException;
import com.innomind.msffinance.web.service.FilterService;

import io.swagger.annotations.ApiOperation;

/**
 * @author Yokesh
 * Controller to get configuration data like.
 * 1) Tenure Type 
 * 2) Interest Type
 * 3) Status Type
 * 4) Document Type
 */
@RestController
@RequestMapping("/api/filter")
public class FilterController {
	
	@Autowired
	FilterService filterService;
	
	/**
	 * To get configuration data.
	 * @return
	 * @throws Exception
	 */
	@ApiOperation(value = "To get Configuration Data", 
			notes="To get configuration data like Tenure, Interest, Status and Document")
	@RequestMapping(value="/", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public Map<String,List<Object>> getFilters() throws MsfFinanceException{
		Map<String,List<Object>> returnList = new HashMap<>();
		try{
			returnList = filterService.getFilters();
		}catch(MsfFinanceException e){
			throw e;
		}
		return returnList;
	}
}

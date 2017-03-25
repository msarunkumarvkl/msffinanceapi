package com.innomind.msffinance.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.innomind.msffinance.web.exceptions.MsfFinanceException;
import com.innomind.msffinance.web.model.CustomClientInterest;
import com.innomind.msffinance.web.model.InterestChart;
import com.innomind.msffinance.web.model.MsfClientInterest;
import com.innomind.msffinance.web.service.InterestService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/interest")
public class InterestController {
	
	@Autowired
	InterestService interestService;
	
	@ApiOperation(value = "To get Interest Details", 
			notes="To get details of Interest to be given between the startdate and enddate."
					+ " For Pagination please provide start and end .")
	@RequestMapping(value="/",method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public List<CustomClientInterest> getInterestDetails(
			@RequestParam(value="startdate",required = true)String startDate,
			@RequestParam(value="enddate",required = true)String endDate,
			@RequestParam(value="start",required = false)String start,
			@RequestParam(value="end",required = false)String end,
			@RequestParam(value="search",required = false)String search)throws MsfFinanceException{
		
		List<CustomClientInterest> returnClientInterest = new ArrayList<>();
		try{
			returnClientInterest = interestService.getInterestDetails(startDate,endDate,start,end,search); 
		}catch(MsfFinanceException e){
			throw e;
		}
		return returnClientInterest;
	}
	
	@ApiOperation(value = "To get Unpaid Interest Details", 
			notes="To get details of Unpaid Interest to be given before the date."
					+ " For Pagination please provide start and end .")
	@RequestMapping(value = "/unpaid", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public List<CustomClientInterest> getUnpaidInterestDetails(
			@RequestParam(value = "date", required = true) String date,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false)String end,
			@RequestParam(value = "search", required = false)String search) throws MsfFinanceException{
		List<CustomClientInterest> unpaidClientInterest = new ArrayList<>();
		try {
			unpaidClientInterest = interestService.getUnpaidInterestDetails(date, start, end, search);
		} catch (MsfFinanceException e) {
			throw e;
		}
		return unpaidClientInterest;
	}
	
	@ApiOperation(value = "To get data for populating chart", 
			notes="To get received and unreceived interest amount for populating chart for the given "
					+ "startdate and enddate.")
	@RequestMapping(value = "/chart", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public InterestChart getInterestChartDetails(
			@RequestParam(value="startdate",required = true)String startDate,
			@RequestParam(value="enddate",required = true)String endDate) throws MsfFinanceException{
		InterestChart interestChartDetails = new InterestChart();
		try {
			interestChartDetails = interestService.getInterestChartDetails(startDate, endDate);
		} catch (MsfFinanceException e) {
			throw e;
		}
		return interestChartDetails;
	}
	
	@ApiOperation(value = "To update the status of the interest details", 
			notes="Update the status of the interest details to paid, unpaid or partially paid")
	@RequestMapping(value="/", method= RequestMethod.PUT, produces="application/json")
	@ResponseBody
	public ResponseEntity<Map<String,String>> updateInterestDetails
		(@RequestBody MsfClientInterest clientInterest) throws MsfFinanceException {
		Map<String, String> returnMap = new HashMap<>();
		try {
			interestService.updateInterest(clientInterest);
			returnMap.put("status", "success");
		} catch(MsfFinanceException e) {
			throw e;
		}
		return new ResponseEntity<>(returnMap, HttpStatus.OK);
	}
}

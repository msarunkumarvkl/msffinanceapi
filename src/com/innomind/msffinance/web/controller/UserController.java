package com.innomind.msffinance.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.innomind.msffinance.web.exceptions.MsfFinanceException;
import com.innomind.msffinance.web.model.CustomClientInterest;
import com.innomind.msffinance.web.model.InterestChart;
import com.innomind.msffinance.web.model.MsfClient;
import com.innomind.msffinance.web.service.UserService;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	@Autowired
	UserService userService;
	
	@ApiOperation(value = "To Add new User", 
			notes="To add one single user. For performance please donot send Photo with this request."
					+ "Upload Photo as a sperate PUT request with the userId you received as response "
					+ "from this call.")
	@RequestMapping(value="",method=RequestMethod.POST, produces="application/json")
	@ResponseBody
	public ResponseEntity<Map<String,Object>> addUser(@RequestBody MsfClient newUser)
			throws MsfFinanceException {
		Map<String,Object> returnObject = new HashMap<>();
		try{
			int userId = userService.addUser(newUser);
			returnObject.put("status", "success");
			returnObject.put("userId", userId);
		}catch(MsfFinanceException e){
			throw e;
		}
		return new ResponseEntity<>(returnObject,HttpStatus.CREATED);
	}
	
	@ApiOperation(value = "To update User Details", 
			notes="To update user details.")
	@RequestMapping(value="", method=RequestMethod.PUT, produces="application/json")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateUser(
			@RequestBody MsfClient msfClient) throws MsfFinanceException{
		Map<String,Object> returnObject = new HashMap<>();
		try{
			userService.updateUser(msfClient);
			returnObject.put("status", "success");
		}catch(MsfFinanceException e){
			throw e;
		}
		return new ResponseEntity<>(returnObject,HttpStatus.OK);
	}
	
	@ApiOperation(value = "To delete User Details", 
			notes = "To delete user details. UserId is mandatory")
	@RequestMapping(value="/{clientId}", method=RequestMethod.DELETE, produces="application/json")
	@ResponseBody
	public ResponseEntity<Map<String,Object>> deleteUser(@PathVariable int clientId)
		throws MsfFinanceException {
		Map<String, Object> returnMap = new HashMap<>();
		try {
			userService.deleteUser(clientId);
			returnMap.put("status", "success");
		} catch(MsfFinanceException e) {
			throw e;
		}
		return new ResponseEntity<>(returnMap, HttpStatus.OK);
	}
	
	@ApiOperation(value = "To add many Users", 
			notes="To add many users. Send User Details as an array.")
	@RequestMapping(value="/",method=RequestMethod.POST, produces="application/json")	
	@ResponseBody
	public ResponseEntity<List<MsfClient>> addUsers(@RequestBody List<MsfClient> newUsers)
			throws MsfFinanceException{
		List<MsfClient> returnClientList = new ArrayList<>();
		try{
			returnClientList = userService.addUsers(newUsers);
		}catch(MsfFinanceException e){
			throw e;
		}
		return new ResponseEntity<>(returnClientList,HttpStatus.CREATED);
	}
	
	@ApiOperation(value = "To get User Details(Lend details)", 
			notes="To get User Details(Lend details) between the month of startdate and enddate."
					+ " For Pagination please provide start and end .")
	@RequestMapping(value="/",method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public List<MsfClient> getClientDetails(
			@RequestParam(value = "startdate", required = false)String startDate,
			@RequestParam(value = "enddate", required = false)String endDate,
			@RequestParam(value = "start", required = false)String start,
			@RequestParam(value = "end", required = false)String end,
			@RequestParam(value = "search", required = false)String search)throws MsfFinanceException{
		
		List<MsfClient> returnClient = new ArrayList<>();
		try{
			returnClient = userService.getClientDetails(startDate,endDate,start,end,search); 
		}catch(MsfFinanceException e){
			throw e;
		}
		return returnClient;
	}
	
	@ApiOperation(value = "To download Excel file",
			notes = "To download Excel File which has Two sheets one for Lend Details and other for Interest details")
	@RequestMapping(value="/export", method = RequestMethod.GET, produces="application/vnd.ms-excel")
	public void exportExcel(
			@RequestParam(value = "startdate", required = false)String startDate,
			@RequestParam(value = "enddate", required = false)String endDate,
			HttpServletResponse response){
			
			HSSFWorkbook workBook = userService.getExcelFile(startDate, endDate);
			response.setHeader("Content-disposition", "attachment; filename=msffinance.xls");
			try {
				workBook.write(response.getOutputStream());
			} catch (IOException e) {
				throw new MsfFinanceException(e.getMessage()); 
			}
	}
	
	@ApiOperation(value = "To get specific User Detail", 
			notes="To get specific User Detail based on clientId. Client Photo is not sent "
					+ "with this request. You need to send seperate request for that.")
	@RequestMapping(value="/{clientId}", method=RequestMethod.GET, produces="application/json")
	@ResponseBody
	public MsfClient getClientDetail(@PathVariable int clientId) 
			throws MsfFinanceException {
		MsfClient returnClient = new MsfClient();
		try {
			returnClient = userService.getClientDetail(clientId);
		} catch (MsfFinanceException e) {
			throw e;
		}
		return returnClient;
	}
	
	@ApiOperation(value = "To get specific User Photo", 
			notes="To get specific User Photo based on clientId.")
	@RequestMapping(value="/{clientId}/photo", method=RequestMethod.GET)
	@ResponseBody
	public MsfClient getClientPhoto
		(@PathVariable int clientId) throws MsfFinanceException {
		MsfClient returnClient = new MsfClient();
		try {
			returnClient = userService.getClientPhoto(clientId);
		} catch (MsfFinanceException e) {
			throw e;
		}
		return returnClient;
	}
	
	@ApiOperation(value = "To get specific User Interest Details", 
			notes="To get specific User Interest Details based on clientId.")
	@RequestMapping(value = "/{clientId}/interest", method = RequestMethod.GET, 
			produces="application/json")
	@ResponseBody
	public List<CustomClientInterest> getClientInterestDetails
		(@PathVariable int clientId) throws MsfFinanceException {
		List<CustomClientInterest> clientInterestDetails = new ArrayList<>();
		try {
			clientInterestDetails = userService.getClientInterestDetails(clientId);
		} catch (MsfFinanceException e) {
			throw e;
		}
		return clientInterestDetails;
	}
	
	@ApiOperation(value = "To get specific User Chart Details", 
			notes="To get specific User Chart Details data for populating chart based on clientId.")
	@RequestMapping(value = "/{clientId}/interest/chart", method=RequestMethod.GET,
			produces="application/json")
	@ResponseBody
	public ResponseEntity<InterestChart> getClientInterestChartDetails
			(@PathVariable int clientId) throws MsfFinanceException {
		InterestChart interestChartDetails = new InterestChart();
		try {
			interestChartDetails = userService.getInterestChartDetails(clientId);
		} catch (MsfFinanceException e) {
			throw e;
		}
		return new ResponseEntity<>(interestChartDetails, HttpStatus.OK);
	}
	
	
}

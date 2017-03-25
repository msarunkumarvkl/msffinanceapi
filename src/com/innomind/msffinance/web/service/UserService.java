package com.innomind.msffinance.web.service;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.innomind.msffinance.web.exceptions.MsfFinanceException;
import com.innomind.msffinance.web.model.CustomClientInterest;
import com.innomind.msffinance.web.model.InterestChart;
import com.innomind.msffinance.web.model.MsfClient;

public interface UserService {

	int addUser(MsfClient newUser)throws MsfFinanceException;

	List<MsfClient> addUsers(List<MsfClient> newUsers)throws MsfFinanceException;

	List<MsfClient> getClientDetails(String startDate, String endDate, 
			String start, String end, String search)throws MsfFinanceException;

	MsfClient getClientDetail(int clientId);

	MsfClient getClientPhoto(int clientId);
	List<CustomClientInterest> getClientInterestDetails(int clientId);

	InterestChart getInterestChartDetails(int clientId);

	void updateUser(MsfClient msfClient);

	void deleteUser(int clientId);

	HSSFWorkbook getExcelFile(String startDate, String endDate);

}

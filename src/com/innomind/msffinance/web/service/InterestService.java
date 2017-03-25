package com.innomind.msffinance.web.service;

import java.util.List;

import com.innomind.msffinance.web.exceptions.MsfFinanceException;
import com.innomind.msffinance.web.model.CustomClientInterest;
import com.innomind.msffinance.web.model.InterestChart;
import com.innomind.msffinance.web.model.MsfClient;
import com.innomind.msffinance.web.model.MsfClientInterest;

public interface InterestService {

	void createInterest(MsfClient msfClient, String interestAmount);

	List<CustomClientInterest> getInterestDetails(String startDate, 
			String endDate, String start, String end, 
			String search)throws MsfFinanceException;

	List<CustomClientInterest> getUnpaidInterestDetails(String date, 
			String start, String end, String search)throws MsfFinanceException;

	InterestChart getInterestChartDetails(String startDate, 
			String endDate) throws MsfFinanceException;

	void updateInterest(MsfClientInterest clientInterest) throws MsfFinanceException;

	int getClientInterestDetails(int clientId);

}

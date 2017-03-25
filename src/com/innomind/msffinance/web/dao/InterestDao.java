package com.innomind.msffinance.web.dao;

import java.util.Date;
import java.util.List;

import com.innomind.msffinance.web.model.CustomClientInterest;
import com.innomind.msffinance.web.model.MsfClientInterest;
import com.innomind.msffinance.web.model.MsfStatus;

public interface InterestDao {

	void addClientInterest(MsfClientInterest clientInterest);

	List<CustomClientInterest> getInterestDetails(Date startDate, 
			Date endDate, int start, int end, String search);

	List<CustomClientInterest> getUnpaidInterestDetails(Date startDate, 
			Integer start, int end, Integer statusId, String search);

	MsfClientInterest getInterestById(Integer interestId);

	void updateClientInterest(MsfClientInterest existingInterest);

	MsfClientInterest getNextInterest(Integer clientId, Date interestDate);

	MsfClientInterest getPreviousInterest(Integer clientId, Date interestDate);

	String getInterestPrevBalDetails(MsfStatus msfStatus, Date startDate, Date endDate, String string);

	String getChartDetails(MsfStatus msfPaidStatus, MsfStatus msfPartialStatus, Date startDate, Date endDate,
			String string);

	int getPaidInterestDetails(int clientId, int paidStatusId, int partiallyPaidStatusId);

}

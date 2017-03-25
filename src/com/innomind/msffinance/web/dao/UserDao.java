package com.innomind.msffinance.web.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.innomind.msffinance.web.model.CustomClientInterest;
import com.innomind.msffinance.web.model.MsfClient;
import com.innomind.msffinance.web.model.MsfClientAltPhone;
import com.innomind.msffinance.web.model.MsfClientDocument;
import com.innomind.msffinance.web.model.MsfStatus;

public interface UserDao {

	MsfClient addUser(MsfClient newClient);

	MsfClient getClient(String clientPhone);

	MsfClientAltPhone addClientAltPhone(MsfClientAltPhone altPhone);

	MsfClientDocument addClientDocument(MsfClientDocument clientDocument);

	List<MsfClient> getClientDetails(Date startDate, Date endDate, int start, int end, String search);

	MsfClient getClient(int clientId);

	MsfClient getClientPhoto(int clientId);
	
	List<CustomClientInterest> getClientInterestDetails(int clientId);

	String getInterestChartDetails(MsfStatus msfStatus, int clientId, String sumColumName);

	String getInterestPrevBalDetails(MsfStatus msfStatus, int clientId, String string);

	void updateUser(MsfClient existingUser);

	MsfClient getFullClient(int clientId);

	Set<MsfClientAltPhone> getClientAltPhone(MsfClient clientId);

	Set<MsfClientDocument> getClientDocuments(MsfClient clientId);

	void deleteUser(MsfClient msfClient);

	void deleteUserAltPhones(Integer clientId);

	void delteUserDocuments(Integer clientId);

	void deleteUserInterestDetails(Integer clientId);

}

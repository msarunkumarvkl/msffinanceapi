package com.innomind.msffinance.web.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.innomind.msffinance.web.dao.FilterDao;
import com.innomind.msffinance.web.dao.InterestDao;
import com.innomind.msffinance.web.exceptions.MsfFinanceException;
import com.innomind.msffinance.web.exceptions.ValidationException;
import com.innomind.msffinance.web.model.CustomClientInterest;
import com.innomind.msffinance.web.model.InterestChart;
import com.innomind.msffinance.web.model.MsfClient;
import com.innomind.msffinance.web.model.MsfClientInterest;
import com.innomind.msffinance.web.model.MsfStatus;
import com.innomind.msffinance.web.model.MsfTenureType;
import com.innomind.msffinance.web.util.MsfFinanceUtil;

@Service("interestService")
@Transactional(propagation = Propagation.REQUIRED)
public class InterestServiceImpl implements InterestService{
	
	@Autowired
	InterestDao interestDao;
	
	@Autowired
	MsfFinanceUtil msfFinanceUtil;
	
	@Autowired
	FilterDao filterDao;
	
	private static final String[] INTEREST_STATUS = new String[]
			{"Paid", "Unpaid", "Partially Paid"};
	
	@Override
	public void createInterest(MsfClient msfClient, String interestAmount) {
		int tenurePeriod = msfClient.getClientTenure();
		final Date createdOn = msfFinanceUtil.getTodayDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(createdOn);
		int tenureTypeId = msfClient.getTenureTypeId();
		MsfTenureType msfTenureType = filterDao.getTenureType(tenureTypeId); 
		String tenureName = msfTenureType.getTenureTypeName();
		for(int i=1;i <= tenurePeriod; i++){
			MsfClientInterest clientInterest = new MsfClientInterest(); 
			clientInterest.setMsfClient(msfClient);
			clientInterest.setCreatedOn(createdOn);
			clientInterest.setInterestAmount(interestAmount);
			clientInterest.setInterestPaidAmount("0");
			clientInterest.setInterestPrevBal("0");
			clientInterest.setInterestBal(interestAmount);
			if("Months".equals(tenureName)){
				calendar.add(Calendar.DATE,+30);
			}else if("Days".equals(tenureName)){
				calendar.add(Calendar.DATE,+1);
			}
			String interestDateString = msfFinanceUtil.formatDate(calendar.getTime());
			Date interestDate = msfFinanceUtil.parseDate(interestDateString);
			clientInterest.setInterestDate(interestDate);
			try {
				MsfStatus msfStatus = filterDao.getMsfStatus(INTEREST_STATUS[1]);
				clientInterest.setStatusId(msfStatus.getStatusId());
				interestDao.addClientInterest(clientInterest);
			} catch (MsfFinanceException e) {
				throw e;
			}
		}
	}

	@Override
	public List<CustomClientInterest> getInterestDetails(String startDateString, String endDateString, 
			String startString, String endString,String search) throws MsfFinanceException {
		Date endDate = new Date();
		Date startDate = new Date();
		int start = 0;
		int end = 0;
		try{
			startDate = msfFinanceUtil.parseUrlDate(startDateString);	
			endDate = msfFinanceUtil.parseUrlDate(endDateString);
		}catch(ValidationException e){
			throw e;
		}
		if( msfFinanceUtil.isNotNull(startString)){
			start = Integer.parseInt(startString);
		}
		if(msfFinanceUtil.isNotNull(endString)){
			end = Integer.parseInt(endString);
		}
		if(end < start){
			throw new ValidationException("Invalid Parameters");
		}
		
		if(startDate.compareTo(endDate) > 0){
			throw new ValidationException("Start Date is after End Date");
		}
		return interestDao.getInterestDetails(startDate, endDate, 
				start, end, search);
	}

	@Override
	public List<CustomClientInterest> getUnpaidInterestDetails(
			String dateString, String startString, String endString, String search)
			throws MsfFinanceException {
		Date date = new Date();
		int start = 0;
		int end = 0;
		try{
			date  = msfFinanceUtil.parseUrlDate(dateString);	
		}catch(ValidationException e){
			throw e;
		}
		if( msfFinanceUtil.isNotNull(startString)){
			start = Integer.parseInt(startString);
		}
		if(msfFinanceUtil.isNotNull(endString)){
			end = Integer.parseInt(endString);
		}
		if(end < start){
			throw new ValidationException("Invalid Parameters");
		}
		MsfStatus msfStatus = filterDao.getMsfStatus(INTEREST_STATUS[1]);
		return interestDao.getUnpaidInterestDetails(date, start, end, 
				msfStatus.getStatusId(), search);
	}

	@Override
	public InterestChart getInterestChartDetails(String startDateString, 
			String endDateString) throws MsfFinanceException {
		Date endDate = new Date();
		Date startDate = new Date();
		try{
			startDate = msfFinanceUtil.parseUrlDate(startDateString);	
			endDate = msfFinanceUtil.parseUrlDate(endDateString);
		}catch(ValidationException e){
			throw e;
		}
		if(startDate.compareTo(endDate) > 0){
			throw new ValidationException("Start Date is after End Date");
		}
		InterestChart interestChart = new InterestChart();
		MsfStatus msfPaidStatus = filterDao.getMsfStatus(INTEREST_STATUS[0]);
		MsfStatus msfPartialStatus = filterDao.getMsfStatus(INTEREST_STATUS[2]);
		String amount = interestDao.getChartDetails(msfPaidStatus, msfPartialStatus,
				startDate, endDate, "interestPaidAmount");
		if (msfFinanceUtil.isNotNull(amount)) {
			interestChart.setReceivedAmount(amount);	
		} else {
			interestChart.setReceivedAmount("0");
		}
		MsfStatus msfUnpaidStatus = filterDao.getMsfStatus(INTEREST_STATUS[1]);
		amount = interestDao.getChartDetails(msfUnpaidStatus, msfPartialStatus, 
				startDate, endDate, "interestBal");
		if (msfFinanceUtil.isNotNull(amount)) {
			interestChart.setNotReceivedAmount(amount);	
		} else {
			interestChart.setNotReceivedAmount("0");
		}
		return interestChart;
	}

	@Override
	public void updateInterest(MsfClientInterest clientInterest) throws MsfFinanceException {
		if(clientInterest.getInterestId() != null && clientInterest.getInterestPaidAmount() != null) {
			MsfClientInterest existingInterest = interestDao.getInterestById(
					clientInterest.getInterestId());
			final Date updatedOn = msfFinanceUtil.getTodayDate();
			int statusId = clientInterest.getStatusId();
			MsfStatus msfStatus = filterDao.getMsfStatus(statusId);
			int interestAmount = Integer.valueOf(
					existingInterest.getInterestBal());
			int interestPaidAmount = Integer.valueOf(
					clientInterest.getInterestPaidAmount());
			//for paid and partially paid
			if (INTEREST_STATUS[2].equals(msfStatus.getStatusName()) || 
					INTEREST_STATUS[0].equals(msfStatus.getStatusName())) {
				checkPreviousInterest(
						existingInterest.getMsfClient().getClientId(), 
						existingInterest.getInterestDate(), msfStatus.getStatusName());
				if (interestPaidAmount > interestAmount) {
					throw new ValidationException("Paid Amount Cannot be greater than " + interestAmount);
				}else {
					int interestBalAmount = 0;
					if(interestPaidAmount < interestAmount) {
						if(!msfStatus.getStatusName().equals(INTEREST_STATUS[2])){
							MsfStatus partiallyPaid = filterDao.getMsfStatus(INTEREST_STATUS[2]);
							statusId = partiallyPaid.getStatusId();
						}
						interestBalAmount = interestAmount - interestPaidAmount;
						updateNextInterest(existingInterest.getMsfClient().getClientId(), 
								existingInterest.getInterestDate(), interestBalAmount, interestPaidAmount);
					} else {
						if(!msfStatus.getStatusName().equals(INTEREST_STATUS[0])){
							MsfStatus partiallyPaid = filterDao.getMsfStatus(INTEREST_STATUS[0]);
							statusId = partiallyPaid.getStatusId();
						}
					}
					existingInterest.setInterestBal(String.valueOf(interestBalAmount));
				}
			} else if(INTEREST_STATUS[1].equals(msfStatus.getStatusName())) {
				interestPaidAmount = 0;
				checkNextInterest(existingInterest.getMsfClient().getClientId(), 
						existingInterest.getInterestDate());
				int interestBal = Integer.parseInt(existingInterest.getInterestAmount())+ 
						Integer.parseInt(existingInterest.getInterestPrevBal());
				existingInterest.setInterestBal(String.valueOf(interestBal));
			}
			existingInterest.setInterestPaidAmount(String.valueOf(interestPaidAmount));
			existingInterest.setStatusId(statusId);
			existingInterest.setUpdatedOn(updatedOn);
			interestDao.updateClientInterest(existingInterest);
		} else {
			throw new ValidationException("Required Parameters Missing");
		}
	}

	private void checkNextInterest(Integer clientId, Date interestDate) {
		MsfClientInterest nextInterest = interestDao.getNextInterest(
				clientId, interestDate);
		if(nextInterest != null) {
			int nextSttausId = nextInterest.getStatusId();
			MsfStatus nextStatus = filterDao.getMsfStatus(nextSttausId);
			if(INTEREST_STATUS[0].equals(nextStatus.getStatusName()) || 
					INTEREST_STATUS[2].equals(nextStatus.getStatusName())) {
				throw new ValidationException("Cannot change to Unpaid as the next Interest is Paid !");
			} else {
				updateNextInterest(clientId, 
						interestDate, 0, 0);
			}
		}
	}

	private void checkPreviousInterest(Integer clientId, Date interestDate, String statusName) {
		MsfClientInterest previousInterest = interestDao.getPreviousInterest(
				clientId, interestDate);
		if(previousInterest != null) {
			int previousStatusId = previousInterest.getStatusId();
			MsfStatus previousStatus = filterDao.getMsfStatus(previousStatusId);
			if("Unpaid".equals(previousStatus.getStatusName())) {
				throw new ValidationException("Cannot change to " 
						+ statusName + " as the previous Interest is Unpaid !");
			}	
		}
	}

	private void updateNextInterest(Integer clientId, Date interestDate, 
			int interestBalAmount, int interestPaidAmount) {
		final Date updatedOn = msfFinanceUtil.getTodayDate();
		MsfClientInterest nextInterest = interestDao.getNextInterest(
				clientId, interestDate);
		int fullAmount = interestBalAmount + interestPaidAmount;
		if(nextInterest != null) {
			int interestAmount = Integer.parseInt(nextInterest.getInterestAmount());
			int totalAmount = interestAmount + interestBalAmount;
			nextInterest.setInterestPrevBal(String.valueOf(interestBalAmount));
			nextInterest.setInterestBal(String.valueOf(totalAmount));
			nextInterest.setUpdatedOn(updatedOn);
			interestDao.updateClientInterest(nextInterest);	
		} else {
			throw new ValidationException("This is your last dew. You need to "
					+ "pay the full Amount " + fullAmount + " !");
		}
	}

	@Override
	public int getClientInterestDetails(int clientId) {
		MsfStatus paidStatus = filterDao.getMsfStatus("Paid");
		int paidStatusId = paidStatus.getStatusId();
		MsfStatus partiallyPaidStatus = filterDao.getMsfStatus("Partially Paid");
		int partiallyPaidStatusId = partiallyPaidStatus.getStatusId(); 
		return interestDao.getPaidInterestDetails(clientId, paidStatusId, partiallyPaidStatusId);
		
	}
}

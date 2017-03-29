package com.innomind.msffinance.web.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.innomind.msffinance.web.dao.FilterDao;
import com.innomind.msffinance.web.dao.UserDao;
import com.innomind.msffinance.web.exceptions.MsfFinanceException;
import com.innomind.msffinance.web.exceptions.ObjectExistsException;
import com.innomind.msffinance.web.exceptions.ObjectNotExistsException;
import com.innomind.msffinance.web.exceptions.ValidationException;
import com.innomind.msffinance.web.model.CustomClientInterest;
import com.innomind.msffinance.web.model.InterestChart;
import com.innomind.msffinance.web.model.MsfClient;
import com.innomind.msffinance.web.model.MsfClientAltPhone;
import com.innomind.msffinance.web.model.MsfClientDocument;
import com.innomind.msffinance.web.model.MsfStatus;
import com.innomind.msffinance.web.util.MsfFinanceUtil;

@Service("userService")
@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	FilterDao filterDao;
	
	@Autowired
	InterestService interestService;
	
	@Autowired
	MsfFinanceUtil msfFinanceUtil;
	
	@Override
	public int addUser(MsfClient newUser) throws MsfFinanceException {
		MsfClient returnClient;
		String clientPhone = newUser.getClientPhone();
		final Date createdOn = msfFinanceUtil.getTodayDate();
		if( msfFinanceUtil.isNotNull(clientPhone)){
			if(newUser.getCreatedOn() == null){
				newUser.setCreatedOn(createdOn);	
			}
			String clientPhoto = newUser.getClientPhotoString();
			if(clientPhoto != null){
				byte[] clientPhototByte = DatatypeConverter.parseBase64Binary(clientPhoto);
				newUser.setClientPhoto(clientPhototByte);
				newUser.setClientPhotoString(null);
			}
			returnClient = userDao.addUser(newUser);

			final Set<MsfClientAltPhone> altPhones = newUser.getMsfClientAltPhones();
			for(MsfClientAltPhone altPhone : altPhones){
				if( msfFinanceUtil.isNotNull(altPhone.getAltPhoneName()) && 
						msfFinanceUtil.isNotNull(altPhone.getAltPhoneNumber())){
					altPhone.setMsfClient(returnClient);
					userDao.addClientAltPhone(altPhone);
				}
			}
			
			final Set<MsfClientDocument> clientDocuments = newUser.getMsfClientDocuments();
			for(MsfClientDocument clientDocument : clientDocuments){
				clientDocument.setMsfClient(returnClient);
				userDao.addClientDocument(clientDocument);
			}
			
			final String interestAmount = newUser.getInterestAmount();
			returnClient.setInterestAmount(interestAmount);
			interestService.createInterest(returnClient,interestAmount);
			return returnClient.getClientId();
		}else {
			throw new ValidationException("Mandatory fields missing!");
		}
	}

	@Override
	public List<MsfClient> addUsers(List<MsfClient> newUsers) throws MsfFinanceException {
		List<MsfClient> returnClientList = new ArrayList<>();
		MsfStatus status = filterDao.getMsfStatus("success");
		for(MsfClient clientDetails : newUsers){
			MsfClient requestClient = clientDetails;
			try{
				addUser(clientDetails);
				requestClient.setStatusId(status.getStatusId());
			}catch(ObjectExistsException|ValidationException e){
				MsfStatus errorStatus = filterDao.getMsfStatus("error");
				requestClient.setStatusId(errorStatus.getStatusId());
				requestClient.setStatusDesc(e.getLocalizedMessage());
			}
			returnClientList.add(requestClient);
		}
		return returnClientList;
	}

	@Override
	public List<MsfClient> getClientDetails(String startDateString, 
			String endDateString, String startString, 
			String endString, String search) throws MsfFinanceException {
		Date endDate = null;
		Date startDate = null;
		int start = 0;
		int end = 0;
		if (search == null){
			if(msfFinanceUtil.isNotNull(startDateString) && 
					msfFinanceUtil.isNotNull(endDateString)){
				try{
					startDate = msfFinanceUtil.parseUrlDate(startDateString);
					endDate = msfFinanceUtil.parseUrlDate(endDateString);
				} catch (ValidationException e){
					throw e;
				}
				if(startDate.compareTo(endDate) > 0) {
					throw new ValidationException("Start Date is after End Date");
				}
			} else{
				throw new ValidationException("Mandatory Fields Missing");
			}	
		}
		if(msfFinanceUtil.isNotNull(startString)){
			start = Integer.parseInt(startString);
		}
		if(msfFinanceUtil.isNotNull(endString)){
			end = Integer.parseInt(endString);
		}
		if(end < start){
			throw new ValidationException("Invalid Parameters");
		}
		return userDao.getClientDetails(startDate,endDate,start,end,search);
	}

	@Override
	public MsfClient getClientDetail(int clientId) {
		MsfClient returnClient = userDao.getClient(clientId);
		if(returnClient != null) {
			returnClient.setMsfClientAltPhones(userDao.getClientAltPhone(returnClient));
			returnClient.setMsfClientDocuments(userDao.getClientDocuments(returnClient));	
		}
		return returnClient;
	}

	@Override
	public MsfClient getClientPhoto(int clientId) {
		return userDao.getClientPhoto(clientId);
	}
	
	@Override
	public List<CustomClientInterest> getClientInterestDetails(int clientId) {
		return userDao.getClientInterestDetails(clientId);
	}

	@Override
	public InterestChart getInterestChartDetails(int clientId) {
		InterestChart interestChart = new InterestChart();
		MsfStatus msfStatus = filterDao.getMsfStatus("Paid");
		String amount = userDao.getInterestChartDetails(msfStatus,clientId, "interestPaidAmount");
		if(msfFinanceUtil.isNotNull(amount)){
			interestChart.setReceivedAmount(amount);	
		} else {
			interestChart.setReceivedAmount("0");
		}
		msfStatus = filterDao.getMsfStatus("Partially Paid");
		amount = userDao.getInterestChartDetails(msfStatus,clientId, "interestPaidAmount");
		if(msfFinanceUtil.isNotNull(amount)){
			int setAmount = Integer.parseInt(interestChart.getReceivedAmount());
			int partialAmount = Integer.parseInt(amount);
			int newAmount = setAmount + partialAmount;
			interestChart.setReceivedAmount(String.valueOf(newAmount));	
		}
		msfStatus = filterDao.getMsfStatus("Unpaid");
		amount = userDao.getInterestChartDetails(msfStatus,clientId, "interestAmount");
		if(msfFinanceUtil.isNotNull(amount)){
			interestChart.setNotReceivedAmount(amount);	
		} else {
			interestChart.setNotReceivedAmount("0");
		}
		amount = userDao.getInterestPrevBalDetails(msfStatus, clientId, "interestPrevBal");
		if(msfFinanceUtil.isNotNull(amount)){
			int setAmount = Integer.parseInt(interestChart.getNotReceivedAmount());
			int partialAmount = Integer.parseInt(amount);
			int newAmount = setAmount + partialAmount;
			interestChart.setNotReceivedAmount(String.valueOf(newAmount));	
		}
		return interestChart;
	}

	@Override
	public void updateUser(MsfClient msfClient) {
		if (msfClient.getClientId() != null) {
			int clientId = msfClient.getClientId();
			MsfClient existingUser = userDao.getFullClient(clientId);
			if(existingUser != null){
				String clientPhoto = msfClient.getClientPhotoString();
				if(clientPhoto != null){
					byte[] clientPhototByte = DatatypeConverter.parseBase64Binary(clientPhoto);
					existingUser.setClientPhoto(clientPhototByte);
					existingUser.setClientPhotoString(null);
				} else {
					if(msfClient.getClientPhoto() != null){
						existingUser.setClientPhoto(msfClient.getClientPhoto());	
					}
				}
				if(msfClient.getClientPhoto() == null) {
					updateClientPhone(msfClient, existingUser);
					updateClientDocuments(msfClient, existingUser);
					System.out.println(isInterestUpdated(msfClient, existingUser));
					if(isInterestUpdated(msfClient, existingUser)) {
						int count = interestService.getClientInterestDetails(clientId);
						if(count > 0) {
							throw new MsfFinanceException("Cannot Update as Part of the Interest are Paid/ PartiallyPaid . "
									+ "Please try deleting this entry and add new Entry");
						} else {
							final String interestAmount = msfClient.getInterestAmount();
							if(msfFinanceUtil.isNotNull(interestAmount)) {
								userDao.deleteUserInterestDetails(existingUser.getClientId());
								interestService.createInterest(msfClient,interestAmount);	
							}
						}
					}
					byte[] clientPhotoByte = existingUser.getClientPhoto();
					existingUser = (MsfClient) msfFinanceUtil.merge(existingUser, msfClient);
					existingUser.setClientPhoto(clientPhotoByte);
				}
				Date updatedOn = msfFinanceUtil.getTodayDate();
				existingUser.setUpdatedOn(updatedOn);
				userDao.updateUser(existingUser);	
			} else {
				throw new ObjectNotExistsException("User not found");
			}
		} else {
			throw new ValidationException("Mandatory Fields Missing");
		}
	}

	private boolean isInterestUpdated(MsfClient msfClient, MsfClient existingUser) {
		boolean returnValue = false;
		if((msfClient.getClientTenure() != 0 && msfClient.getClientTenure() 
				!= existingUser.getClientTenure()) || (msfClient.getTenureTypeId() != 0 
				&& msfClient.getTenureTypeId() != existingUser.getTenureTypeId())) {
			returnValue = true;
		}
		if((msfClient.getInterestTypeId() != 0 && msfClient.getInterestTypeId() 
				!= existingUser.getInterestTypeId()) || (
				msfFinanceUtil.isNotNull(msfClient.getClientInterest())  && 
				!msfClient.getClientInterest().equals(existingUser.getClientInterest()))) {
			returnValue = true;
		}
		return returnValue;
	}

	private void updateClientDocuments(MsfClient msfClient, MsfClient existingUser) {
		if(msfClient.getMsfClientDocuments() != null &&
				msfClient.getMsfClientDocuments().size() > 0) {
			userDao.delteUserDocuments(existingUser.getClientId());
		}
	}

	private void updateClientPhone(MsfClient msfClient, MsfClient existingUser) {
		if(msfClient.getMsfClientAltPhones() != null && 
				msfClient.getMsfClientAltPhones().size() > 0) {
			userDao.deleteUserAltPhones(existingUser.getClientId());
		}
	}

	@Override
	public void deleteUser(int clientId) {
		MsfClient msfClient = userDao.getClient(clientId);
		if(msfClient != null) {
			userDao.deleteUser(msfClient);
		} else {
			throw new ObjectNotExistsException("User not found");
		}
	}

	@Override
	public HSSFWorkbook getExcelFile(String startDate, String endDate) {
		List<MsfClient> msfClient = getClientDetails(startDate, endDate, null, 
				null, null);
		List<CustomClientInterest> msfClientInterest = interestService.
				getInterestDetails(startDate, endDate, null, null, null);
		HSSFWorkbook workBook = new HSSFWorkbook();
		
		HSSFFont font = workBook.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		
		HSSFFont normalFont = workBook.createFont();
		normalFont.setBoldweight(HSSFFont.BOLDWEIGHT_NORMAL);
		
		HSSFCellStyle headerStyle = workBook.createCellStyle();
		font.setColor(HSSFColor.WHITE.index);
		headerStyle.setFont(font);
		headerStyle.setFillForegroundColor(HSSFColor.BROWN.index);
		headerStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		HSSFCellStyle normalStyle = workBook.createCellStyle();
		normalFont.setColor(HSSFColor.BLACK.index);
		normalStyle.setFont(normalFont);
		normalStyle.setFillForegroundColor(HSSFColor.WHITE.index);
		normalStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		HSSFCellStyle paidStyle = workBook.createCellStyle();
		normalFont.setColor(HSSFColor.WHITE.index);
		paidStyle.setFont(normalFont);
		paidStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		paidStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		HSSFCellStyle unPaidStyle = workBook.createCellStyle();
		unPaidStyle.setFont(normalFont);
		unPaidStyle.setFillForegroundColor(HSSFColor.RED.index);
		unPaidStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		HSSFCellStyle partiallyPaidStyle = workBook.createCellStyle();
		partiallyPaidStyle.setFont(normalFont);
		partiallyPaidStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		partiallyPaidStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		HSSFCellStyle dateStyle = workBook.createCellStyle();
		CreationHelper createHelper = workBook.getCreationHelper();
		dateStyle.setDataFormat(
		    createHelper.createDataFormat().getFormat("dd/mm/yyyy"));
		
		if(!msfClient.isEmpty()) {
			String[] userHeader = {"Name", "Address", "Phone", "Amount", "Tenure Type", "Tenure",
					"Interest Type", "Interest", "Created On"};
			HSSFSheet sheet1 = workBook.createSheet("Interest");
			int rownum =0;
			Row row = sheet1.createRow(rownum);
			int cellnum =0;
			for(String header : userHeader) {
				Cell cell =  row.createCell(cellnum);
				cell.setCellStyle(headerStyle);
				cell.setCellValue(header);
				cellnum++;
			}
			rownum++;
			for(MsfClient clientDetails : msfClient) {
				row = sheet1.createRow(rownum);
				row.setRowStyle(normalStyle);
				cellnum = 0;
				Cell cell = row.createCell(cellnum);
				cell.setCellValue(clientDetails.getClientName());
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(clientDetails.getClientAddress());
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(clientDetails.getClientPhone());
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(clientDetails.getClientAmount());
				cellnum++;
				cell = row.createCell(cellnum);
				if(clientDetails.getTenureTypeId() == 1)
					cell.setCellValue("Months");
				else if(clientDetails.getTenureTypeId() == 2)
					cell.setCellValue("Days");
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(clientDetails.getClientTenure());
				cellnum++;
				cell = row.createCell(cellnum);
				if(clientDetails.getInterestTypeId() == 1)
					cell.setCellValue("Percentage");
				else if(clientDetails.getInterestTypeId() == 2)
					cell.setCellValue("Amount");
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(clientDetails.getClientInterest());
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(clientDetails.getCreatedOn());
				cell.setCellStyle(dateStyle);
				rownum++;
			}
		}
		
		if(!msfClientInterest.isEmpty()) {
			String[] userHeader = {"Name", "Phone", "Status", "Amount", "Date",
					"Paid Amount", "Prev Balance", "Total"};
			HSSFSheet sheet2 = workBook.createSheet("Lend");	
			int rownum =0;
			Row row = sheet2.createRow(rownum);
			int cellnum =0;
			for(String header : userHeader) {
				Cell cell =  row.createCell(cellnum);
				cell.setCellStyle(headerStyle);
				cell.setCellValue(header);
				cellnum++;
			}
			rownum++;
			for(CustomClientInterest clientInterest : msfClientInterest) {
				row = sheet2.createRow(rownum);
				row.setRowStyle(normalStyle);
				cellnum = 0;
				Cell cell = row.createCell(cellnum);
				cell.setCellValue(clientInterest.getClientName());
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(clientInterest.getClientPhone());
				cellnum++;
				cell = row.createCell(cellnum);
				int statusId = clientInterest.getStatusId();
				if(statusId == 1){
					cell.setCellValue("Paid");
					cell.setCellStyle(paidStyle);
				} else if(statusId == 2) {
					cell.setCellValue("Unpaid");
					cell.setCellStyle(unPaidStyle);
				} else if(statusId == 6) {
					cell.setCellValue("Partially Paid");
					cell.setCellStyle(partiallyPaidStyle);
				}
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(clientInterest.getInterestAmount());
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(clientInterest.getInterestDate());
				cell.setCellStyle(dateStyle);
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(clientInterest.getInterestPaidAmount());
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(clientInterest.getInterestPrevBal());
				cellnum++;
				cell = row.createCell(cellnum);
				cell.setCellValue(Integer.parseInt(clientInterest.getInterestPrevBal()) +
						Integer.parseInt(clientInterest.getInterestAmount()));
				rownum++;
			}
		}
		return workBook;
	}
}

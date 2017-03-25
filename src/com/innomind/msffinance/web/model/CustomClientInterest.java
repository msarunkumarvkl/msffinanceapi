package com.innomind.msffinance.web.model;

import java.util.Date;

public class CustomClientInterest {
	
	private String clientName;
	private String clientPhone;
	private Integer interestId;
	private Integer clientId;
	private Integer statusId;
	private String interestAmount;
	private Date interestDate;
	private String interestPaidAmount;
	private String interestPrevBal;
	private String interestBal;
	private Date createdOn;
	private Date updatedOn;
	private byte[] clientPhoto;
	
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getClientPhone() {
		return clientPhone;
	}
	public void setClientPhone(String clientPhone) {
		this.clientPhone = clientPhone;
	}
	public Integer getInterestId() {
		return interestId;
	}
	public void setInterestId(Integer interestId) {
		this.interestId = interestId;
	}
	public Integer getClientId() {
		return clientId;
	}
	public void setClientId(Integer clientId) {
		this.clientId = clientId;
	}
	public Integer getStatusId() {
		return statusId;
	}
	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}
	public String getInterestAmount() {
		return interestAmount;
	}
	public void setInterestAmount(String interestAmount) {
		this.interestAmount = interestAmount;
	}
	public Date getInterestDate() {
		return interestDate;
	}
	public void setInterestDate(Date interestDate) {
		this.interestDate = interestDate;
	}
	public String getInterestPaidAmount() {
		return interestPaidAmount;
	}
	public void setInterestPaidAmount(String interestPaidAmount) {
		this.interestPaidAmount = interestPaidAmount;
	}
	
	public String getInterestPrevBal() {
		return interestPrevBal;
	}
	public void setInterestPrevBal(String interestPrevBal) {
		this.interestPrevBal = interestPrevBal;
	}
	
	public String getInterestBal() {
		return interestBal;
	}
	public void setInterestBal(String interestBal) {
		this.interestBal = interestBal;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public Date getUpdatedOn() {
		return updatedOn;
	}
	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}
	public byte[] getClientPhoto() {
		return clientPhoto;
	}
	public void setClientPhoto(byte[] clientPhoto) {
		this.clientPhoto = clientPhoto;
	}
	
	
	
}

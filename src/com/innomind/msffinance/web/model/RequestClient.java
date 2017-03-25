package com.innomind.msffinance.web.model;

import java.util.HashSet;
import java.util.Set;

public class RequestClient {
	private MsfClient msfClient;
	private Set<MsfClientAltPhone> msfClientAltPhone = new HashSet<>();
	private Set<MsfClientDocument> msfClientDocument= new HashSet<>();
	private String interestAmount;
	private String clientPhoto;
	private int statusId;
	private String statusDesc;
	
	public int getStatusId() {
		return statusId;
	}
	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}
	public String getStatusDesc() {
		return statusDesc;
	}
	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}
	public String getClientPhoto() {
		return clientPhoto;
	}
	public void setClientPhoto(String clientPhoto) {
		this.clientPhoto = clientPhoto;
	}
	public String getInterestAmount() {
		return interestAmount;
	}
	public void setInterestAmount(String interestAmount) {
		this.interestAmount = interestAmount;
	}
	public MsfClient getMsfClient() {
		return msfClient;
	}
	public void setMsfClient(MsfClient msfClient) {
		this.msfClient = msfClient;
	}
	public Set<MsfClientAltPhone> getMsfClientAltPhone() {
		return msfClientAltPhone;
	}
	public void setMsfClientAltPhone(Set<MsfClientAltPhone> msfClientAltPhone) {
		this.msfClientAltPhone = msfClientAltPhone;
	}
	public Set<MsfClientDocument> getMsfClientDocument() {
		return msfClientDocument;
	}
	public void setMsfClientDocument(Set<MsfClientDocument> msfClientDocument) {
		this.msfClientDocument = msfClientDocument;
	}
	
	
	
}

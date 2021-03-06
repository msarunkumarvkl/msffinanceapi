package com.innomind.msffinance.web.model;
// Generated Aug 14, 2016 4:56:02 PM by Hibernate Tools 4.3.1.Final

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * MsfStatus generated by hbm2java
 */
@Entity
@Table(name = "msf_status", catalog = "msfinance")
public class MsfStatus implements java.io.Serializable {

	private Integer statusId;
	private String statusName;

	public MsfStatus() {
	}

	public MsfStatus(String statusName) {
		this.statusName = statusName;
	}

	/*public MsfStatus(String statusName, Set<MsfClientInterest> msfClientInterests) {
		this.statusName = statusName;
		this.msfClientInterests = msfClientInterests;
	}*/

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "status_id", unique = true, nullable = false)
	public Integer getStatusId() {
		return this.statusId;
	}

	public void setStatusId(Integer statusId) {
		this.statusId = statusId;
	}

	@Column(name = "status_name", nullable = false, length = 15)
	public String getStatusName() {
		return this.statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

/*	@OneToMany(fetch = FetchType.LAZY, mappedBy = "msfStatus")
	public Set<MsfClientInterest> getMsfClientInterests() {
		return this.msfClientInterests;
	}

	public void setMsfClientInterests(Set<MsfClientInterest> msfClientInterests) {
		this.msfClientInterests = msfClientInterests;
	}*/

}

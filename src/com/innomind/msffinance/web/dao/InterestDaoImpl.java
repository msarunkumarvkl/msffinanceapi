package com.innomind.msffinance.web.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.innomind.msffinance.web.model.CustomClientInterest;
import com.innomind.msffinance.web.model.MsfClientInterest;
import com.innomind.msffinance.web.model.MsfStatus;

@Repository("interestDao")
@Transactional(propagation=Propagation.MANDATORY)
public class InterestDaoImpl extends AbstractDao implements InterestDao {

	@Override
	public void addClientInterest(MsfClientInterest clientInterest) {
		getSession().save(clientInterest);
	}

	@Override
	public List<CustomClientInterest> getInterestDetails(Date startDate, Date endDate, int start, 
			int end, String search) {
		Criteria criteria = getSession().createCriteria(MsfClientInterest.class,"Client_Interest");
		criteria.createAlias("Client_Interest.msfClient", "Msf_Client");
		
		ProjectionList projList = getProjectionList();
		
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(CustomClientInterest.class));
		
		if(start != 0){
			criteria.setFirstResult(start);
		}
		if(end != 0){
			criteria.setMaxResults(end);
		}
		
		if(search != null){
			Criterion nameCriterion = Restrictions.like("Msf_Client.clientName", search,MatchMode.ANYWHERE);
			Criterion phoneCriterion = Restrictions.like("Msf_Client.clientPhone", search,MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(nameCriterion,phoneCriterion));
		}
		criteria.addOrder(Order.asc("Client_Interest.interestDate"));
		criteria.addOrder(Order.desc("Client_Interest.statusId"));
		criteria.add(Restrictions.between("Client_Interest.interestDate", startDate, endDate));
		return criteria.list();
	}

	private ProjectionList getProjectionList() {
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("Client_Interest.interestId"),"interestId");
		projList.add(Projections.property("Client_Interest.statusId"),"statusId");
		projList.add(Projections.property("Client_Interest.interestAmount"),"interestAmount");
		projList.add(Projections.property("Client_Interest.interestDate"),"interestDate");
		projList.add(Projections.property("Client_Interest.interestPaidAmount"),"interestPaidAmount");
		projList.add(Projections.property("Client_Interest.interestPrevBal"),"interestPrevBal");
		projList.add(Projections.property("Client_Interest.interestBal"),"interestBal");
		projList.add(Projections.property("Client_Interest.createdOn"),"createdOn");
		projList.add(Projections.property("Client_Interest.updatedOn"),"updatedOn");
		projList.add(Projections.property("Msf_Client.clientId"),"clientId");
		projList.add(Projections.property("Msf_Client.clientName"),"clientName");
		projList.add(Projections.property("Msf_Client.clientPhone"),"clientPhone");
		return projList;
	}

	@Override
	public List<CustomClientInterest> getUnpaidInterestDetails(Date startDate, 
			Integer start, int end, Integer statusId, String search) {
		Criteria criteria = getSession().createCriteria(MsfClientInterest.class,"Client_Interest");
		criteria.createAlias("Client_Interest.msfClient", "Msf_Client");
		ProjectionList projList = getProjectionList();
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(CustomClientInterest.class));
		if(start != 0){
			criteria.setFirstResult(start);
		}
		if(end != 0){
			criteria.setMaxResults(end);
		}
		
		if(search != null){
			Criterion nameCriterion = Restrictions.like("Msf_Client.clientName", search,MatchMode.ANYWHERE);
			Criterion phoneCriterion = Restrictions.like("Msf_Client.clientPhone", search,MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(nameCriterion,phoneCriterion));
		}
		Criterion statusCriterian = Restrictions.eq("Client_Interest.statusId", statusId);
		Criterion dateCriterion = Restrictions.lt("Client_Interest.interestDate", startDate);
		criteria.add((Restrictions.and(statusCriterian,dateCriterion)));
		return criteria.list();
	}

	@Override
	public MsfClientInterest getInterestById(Integer interestId) {
		Criteria criteria = getSession().createCriteria(MsfClientInterest.class);
		criteria.add(Restrictions.eq("interestId", interestId));
		return (MsfClientInterest) criteria.uniqueResult();
	}

	@Override
	public void updateClientInterest(MsfClientInterest existingInterest) {
		getSession().update(existingInterest);
	}

	@Override
	public MsfClientInterest getNextInterest(Integer clientId, Date interestDate) {
		Criteria criteria = getSession().createCriteria(MsfClientInterest.class,"Client_Interest");
		criteria.createAlias("Client_Interest.msfClient", "Msf_Client");
		criteria.setFirstResult(0);
		criteria.setMaxResults(1);
		criteria.add(Restrictions.eq("Msf_Client.clientId", clientId));
		criteria.add(Restrictions.gt("Client_Interest.interestDate", interestDate));
		List<MsfClientInterest> clientInterestList = criteria.list();
		if(!clientInterestList.isEmpty()) {
			return clientInterestList.get(0);	
		} else {
			return null;
		}
	}

	@Override
	public MsfClientInterest getPreviousInterest(Integer clientId, Date interestDate) {
		Criteria criteria = getSession().createCriteria(MsfClientInterest.class,"Client_Interest");
		criteria.createAlias("Client_Interest.msfClient", "Msf_Client");
		criteria.setFirstResult(0);
		criteria.setMaxResults(1);
		criteria.add(Restrictions.eq("Msf_Client.clientId", clientId));
		criteria.add(Restrictions.lt("Client_Interest.interestDate", interestDate));
		List<MsfClientInterest> clientInterestList = criteria.list();
		if(!clientInterestList.isEmpty()) {
			return clientInterestList.get(0);	
		} else {
			return null;
		}
	}

	@Override
	public String getInterestPrevBalDetails(MsfStatus msfStatus, Date startDate,
			Date endDate, String sumColumn) {
		Criteria criteria = getSession().createCriteria(MsfClientInterest.class,"Client_Interest");
		criteria.setProjection(Projections.sum(sumColumn));
		criteria.add(Restrictions.eq("statusId", msfStatus.getStatusId()));
		criteria.add(Restrictions.between("interestDate", startDate, endDate));
		criteria.add(Restrictions.isNotNull("interestPrevBal"));
		return (String)criteria.uniqueResult();
	}

	@Override
	public String getChartDetails(MsfStatus msfStatus, MsfStatus msfPartialStatus, 
			Date startDate, Date endDate,
			String sumColumn) {
		Criteria criteria = getSession().createCriteria(MsfClientInterest.class,"Client_Interest");
		criteria.setProjection(Projections.sum(sumColumn));
		Criterion paidCriterion = Restrictions.eq("statusId", msfStatus.getStatusId());
		Criterion partiallyPaidCriterion = Restrictions.eq("statusId", msfPartialStatus.getStatusId());
		criteria.add(Restrictions.or(paidCriterion, partiallyPaidCriterion));
		criteria.add(Restrictions.between("interestDate", startDate, endDate));
		return (String)criteria.uniqueResult();
	}

	@Override
	public int getPaidInterestDetails(int clientId, int paidStatusId, 
			int partiallyPaidStatusId) {
		Criteria criteria = getSession().createCriteria(MsfClientInterest.class, "Client_Interest");
		criteria.add(Restrictions.eq("Client_Interest.msfClient.clientId", clientId));
		Criterion paidCriterion = Restrictions.eq("statusId", paidStatusId);
		Criterion partiallyPaidCriterion = Restrictions.eq("statusId", partiallyPaidStatusId);
		criteria.add(Restrictions.or(paidCriterion, partiallyPaidCriterion));
		List<MsfClientInterest> clientInterest = criteria.list();
		return clientInterest.size();
	}
}

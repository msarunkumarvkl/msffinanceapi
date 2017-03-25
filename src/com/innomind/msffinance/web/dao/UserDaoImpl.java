package com.innomind.msffinance.web.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.innomind.msffinance.web.model.CustomClientInterest;
import com.innomind.msffinance.web.model.MsfClient;
import com.innomind.msffinance.web.model.MsfClientAltPhone;
import com.innomind.msffinance.web.model.MsfClientDocument;
import com.innomind.msffinance.web.model.MsfClientInterest;
import com.innomind.msffinance.web.model.MsfStatus;


@Repository("userDao")
@Transactional(propagation=Propagation.MANDATORY)
public class UserDaoImpl extends AbstractDao implements UserDao {

	@Override
	public MsfClient addUser(MsfClient newClient) {
		getSession().save(newClient);
		return newClient;
	}

	@Override
	public MsfClient getClient(String clientPhone) {
		Criteria criteria = getSession().createCriteria(MsfClient.class);
		criteria.add(Restrictions.eq("clientPhone",clientPhone));
		return (MsfClient) criteria.uniqueResult();
	}
	
	@Override
	public MsfClient getClient(int clientId) {
		Criteria criteria = getSession().createCriteria(MsfClient.class,"MsfClient");
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("clientId"),"clientId");
		projList.add(Projections.property("clientName"),"clientName");
		projList.add(Projections.property("clientPhone"),"clientPhone");
		projList.add(Projections.property("clientAddress"),"clientAddress");
		projList.add(Projections.property("clientAmount"),"clientAmount");
		projList.add(Projections.property("tenureTypeId"), "tenureTypeId");
		projList.add(Projections.property("clientTenure"), "clientTenure");
		projList.add(Projections.property("interestTypeId"), "interestTypeId");
		projList.add(Projections.property("clientInterest"), "clientInterest");
		projList.add(Projections.property("createdOn"), "createdOn");
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(MsfClient.class));
		criteria.add(Restrictions.eq("clientId",clientId));
		return (MsfClient) criteria.uniqueResult();
	}
	
	@Override
	public MsfClient getFullClient(int clientId) {
		Criteria criteria = getSession().createCriteria(MsfClient.class);
		criteria.add(Restrictions.eq("clientId",clientId));
		return (MsfClient) criteria.uniqueResult();
	}
	
	@Override
	public MsfClient getClientPhoto(int clientId) {
		Criteria criteria = getSession().createCriteria(MsfClient.class);
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property("clientId"),"clientId");
		projList.add(Projections.property("clientPhoto"),"clientPhoto");
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(MsfClient.class));
		criteria.add(Restrictions.eq("clientId",clientId));
		return (MsfClient) criteria.uniqueResult();
	}
	
	@Override
	public MsfClientAltPhone addClientAltPhone(MsfClientAltPhone altPhone) {
		getSession().save(altPhone);
		return altPhone;
	}

	@Override
	public MsfClientDocument addClientDocument(MsfClientDocument clientDocument) {
		getSession().save(clientDocument);
		return clientDocument;
	}

	@Override
	public List<MsfClient> getClientDetails(Date startDate, Date endDate, int start, int end, String search) {
		String hql = "SELECT client.clientId AS clientId, client.clientName AS clientName,"
				+ " client.clientPhone AS clientPhone,client.clientAddress as clientAddress,"
				+ " client.tenureTypeId as tenureTypeId,client.clientTenure as clientTenure, "
				+ " client.interestTypeId as interestTypeId, client.clientAmount AS clientAmount, "
				+ " client.clientInterest as clientInterest,client.createdOn AS createdOn,"
				+ " DATEDIFF(client.createdOn, CURDATE()) AS diff FROM MsfClient AS client";
		
		if (search != null) {
			hql += " WHERE (client.clientName LIKE '%" + search + "%' OR client.clientPhone LIKE '%" 
					+ search + "%')";
		} else {
			if(startDate.compareTo(endDate) == 0) {
				hql += " WHERE MONTH(client.createdOn) = :startdate";
			} else {
				hql += " WHERE MONTH(client.createdOn) BETWEEN :startdate AND :enddate";	
			}			
		}
		
		hql += " ORDER BY diff";
		Query query = getSession().createQuery(hql);
		if (search == null){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			query.setParameter("startdate", calendar.get(calendar.MONTH) + 1);
			if(startDate.compareTo(endDate) != 0) {
				calendar.setTime(endDate);
				query.setParameter("enddate", calendar.get(calendar.MONTH) + 1);	
			}
		}
		if(start != 0){
			query.setFirstResult(start);
		}
		if(end != 0){
			query.setMaxResults(end);
		}
		query.setResultTransformer(Transformers.aliasToBean(MsfClient.class));
		return query.list();
	}
	
	@Override
	public List<CustomClientInterest> getClientInterestDetails(int clientId) {
		Criteria criteria = getSession().createCriteria(MsfClientInterest.class,"Client_Interest");
		criteria.createAlias("Client_Interest.msfClient", "Msf_Client");
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
		criteria.setProjection(projList);
		criteria.setResultTransformer(Transformers.aliasToBean(CustomClientInterest.class));
		criteria.add(Restrictions.eq("Msf_Client.clientId", clientId));
		criteria.addOrder(Order.asc("Client_Interest.interestDate"));
		return criteria.list();
	}

	@Override
	public String getInterestChartDetails(MsfStatus msfStatus, int clientId, String sumColumName) {
		Criteria criteria = getSession().createCriteria(MsfClientInterest.class,"Client_Interest");
		criteria.createAlias("Client_Interest.msfClient", "Msf_Client");
		criteria.setProjection(Projections.sum(sumColumName));
		criteria.add(Restrictions.eq("Client_Interest.statusId", msfStatus.getStatusId()));
		criteria.add(Restrictions.eq("Msf_Client.clientId", clientId));
		String result = (String)criteria.uniqueResult();
		return result;
	}

	@Override
	public String getInterestPrevBalDetails(MsfStatus msfStatus, int clientId, String sumColumName) {
		Criteria criteria = getSession().createCriteria(MsfClientInterest.class,"Client_Interest");
		criteria.createAlias("Client_Interest.msfClient", "Msf_Client");
		criteria.setProjection(Projections.sum(sumColumName));
		criteria.add(Restrictions.eq("Client_Interest.statusId", msfStatus.getStatusId()));
		criteria.add(Restrictions.eq("Msf_Client.clientId", clientId));
		criteria.add(Restrictions.isNotNull("Client_Interest.interestPrevBal"));
		return (String)criteria.uniqueResult();
	}

	@Override
	public void updateUser(MsfClient existingUser) {
		getSession().update(existingUser);
	}

	@Override
	public Set<MsfClientAltPhone> getClientAltPhone(MsfClient msfClient) {
		Criteria criteria = getSession().createCriteria(MsfClientAltPhone.class);
		criteria.add(Restrictions.eq("msfClient", msfClient));
		return new HashSet<>(criteria.list());
	}

	@Override
	public Set<MsfClientDocument> getClientDocuments(MsfClient msfClient) {
		Criteria criteria = getSession().createCriteria(MsfClientDocument.class);
		criteria.add(Restrictions.eq("msfClient", msfClient));
		return new HashSet<>(criteria.list());
	}

	@Override
	public void deleteUser(MsfClient msfClient) {
		getSession().delete(msfClient);
	}

	@Override
	public void deleteUserAltPhones(Integer clientId) {
		String hql = "DELETE FROM MsfClientAltPhone altPhones where "
				+ "altPhones.msfClient.clientId = " + clientId;
		Query query = getSession().createQuery(hql);
		query.executeUpdate();
	}

	@Override
	public void delteUserDocuments(Integer clientId) {
		String hql = "DELETE FROM MsfClientDocument documents where "
				+ "documents.msfClient.clientId = " + clientId;
		Query query = getSession().createQuery(hql);
		query.executeUpdate();
	}

	@Override
	public void deleteUserInterestDetails(Integer clientId) {
		String hql = "DELETE FROM MsfClientInterest interest where "
				+ "interest.msfClient.clientId = " + clientId;
		Query query = getSession().createQuery(hql);
		query.executeUpdate();
	}
}

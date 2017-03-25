package com.innomind.msffinance.web.dao;


import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.innomind.msffinance.web.exceptions.MsfFinanceException;
import com.innomind.msffinance.web.model.*;


@Repository("filterDao")
@Transactional(propagation=Propagation.MANDATORY)
public class FilterDaoImpl extends AbstractDao implements FilterDao{

	@Override
	public List<Object> getTenureTypes() throws MsfFinanceException {
		Criteria criteria = getSession().createCriteria(MsfTenureType.class);
		return criteria.list();
	}

	@Override
	public List<Object> getDocumentTypes() throws MsfFinanceException {
		Criteria criteria = getSession().createCriteria(MsfDocumentType.class);
		return criteria.list();
	}

	@Override
	public List<Object> getInterestTypes() throws MsfFinanceException {
		Criteria criteria = getSession().createCriteria(MsfInterestType.class);
		return criteria.list();
	}

	@Override
	public MsfStatus getMsfStatus(String statusName) {
		Criteria criteria = getSession().createCriteria(MsfStatus.class);
		criteria.add(Restrictions.eq("statusName", statusName));
		return (MsfStatus) criteria.uniqueResult();
	}

	@Override
	public List<Object> getFilterTypes() {
		Criteria criteria = getSession().createCriteria(MsfStatus.class);
		return criteria.list();
	}

	@Override
	public MsfTenureType getTenureType(int tenureTypeId) {
		Criteria criteria = getSession().createCriteria(MsfTenureType.class);
		criteria.add(Restrictions.eq("tenureTypeId", tenureTypeId));
		return (MsfTenureType) criteria.uniqueResult();		
	}

	@Override
	public MsfStatus getMsfStatus(int statusId) {
		Criteria criteria = getSession().createCriteria(MsfStatus.class);
		criteria.add(Restrictions.eq("statusId", statusId));
		return (MsfStatus) criteria.uniqueResult();
	}
	
}

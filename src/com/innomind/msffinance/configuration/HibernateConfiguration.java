package com.innomind.msffinance.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class HibernateConfiguration {
	
	@Bean
	protected DataSource dataSource(){
		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
		return dsLookup.getDataSource("java:jboss/datasources/MySQLDS");
	}
	
	@Bean 
	protected LocalSessionFactoryBean sessionFactory(){
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setPackagesToScan(new String[] {"com.innomind.msffinance.web.model"});
		sessionFactory.setHibernateProperties(hibernateProperties());
		return sessionFactory;
	}
	
	protected Properties hibernateProperties() {
		Properties hibernateProp = new Properties();
		hibernateProp.put("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
		hibernateProp.put("hibernate.show_sql", "true");
		hibernateProp.put("hibernate.jdbc.batch_size", "25");
		hibernateProp.put("hibernate.order_inserts", "true");
		hibernateProp.put("hibernate.order_updates", "true");
		hibernateProp.put("hibernate.jdbc.batch_versioned_data", "true");
		hibernateProp.put("hibernate.format_sql", "false");
		hibernateProp.put("hibernate.hbm2dll", "auto");
		hibernateProp.put("hibernate.generate_statistics", "true");
		return hibernateProp;
	}
	
	@Bean
	@Autowired
	protected HibernateTransactionManager transactionManager(SessionFactory s) {
		 HibernateTransactionManager txManager = new HibernateTransactionManager();
	     txManager.setSessionFactory(s);
	     return txManager;
	}
}

package com.tnt.gestionproj.jbpm;

import java.io.Serializable;
import java.util.Properties;

import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.TransactionManager;

import org.drools.KnowledgeBaseFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.persistence.jta.JtaTransactionManager;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.jbpm.persistence.JpaProcessPersistenceContextManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

//@Component
public class EnvironmentProvider_ implements Serializable {
	//@PersistenceUnit(unitName = "org.jbpm.persistence.jpa")
	@Autowired
	@Qualifier(value="jbpmEMF")
	private EntityManagerFactory emf;
	
	public void setEmf(EntityManagerFactory emf) {
		this.emf = emf;
	}

	@Resource(mappedName = "java:jboss/TransactionManager")
	private TransactionManager tm;
	

	private transient Environment env = null;

	public Environment getEnvironment() {

		if(env == null) {
			 //emf = Persistence.createEntityManagerFactory( "org.jbpm.persistence.jpa" );

			env = KnowledgeBaseFactory.newEnvironment();
			env.set(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER, emf);
			env.set(EnvironmentName.APP_SCOPED_ENTITY_MANAGER, emf);

			JpaProcessPersistenceContextManager manager =
					new JpaProcessPersistenceContextManager(env);
			env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, manager);

			JtaTransactionManager transactionManager = new JtaTransactionManager(null,null,tm);
			env.set(EnvironmentName.TRANSACTION_MANAGER, transactionManager);
		}

		return env;
	}
	
	public Environment getEnvironment2() {
		
		EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.jbpm.persistence.jpa" );

        Environment env = KnowledgeBaseFactory.newEnvironment();

        env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, emf );
        env.set( EnvironmentName.TRANSACTION_MANAGER, getTransactionManager() );
        env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");

        KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        

		if(env == null) {
			 //emf = Persistence.createEntityManagerFactory( "org.jbpm.persistence.jpa" );

			env = KnowledgeBaseFactory.newEnvironment();
			env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emf);
			env.set( EnvironmentName.TRANSACTION_MANAGER, tm );

			JpaProcessPersistenceContextManager manager =
					new JpaProcessPersistenceContextManager(env);
			env.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER, manager);

			JtaTransactionManager transactionManager = new JtaTransactionManager(null,null,tm);
			env.set(EnvironmentName.TRANSACTION_MANAGER, transactionManager);
		}

		return env;
	}
	
	public static TransactionManager getTransactionManager()   {      
        TransactionManager tm  = null;
        try {
        InitialContext ic = new InitialContext();      
        tm  = (TransactionManager) ic.lookup("java:jboss/TransactionManager");
        if (tm == null) throw new IllegalStateException("Couldn't find the transaction mamager in JNDI");      
        }catch (Exception exc) {
            exc.printStackTrace();
        }
        return tm;  
    }
}

package com.tnt.gestionproj.jbpm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.transaction.TransactionManager;

import org.drools.KnowledgeBaseFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.workflow.instance.WorkflowProcessInstanceUpgrader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.stereotype.Component;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.jta.JtaTransactionManager;

import bitronix.tm.TransactionManagerServices;

@Component(value="knowledgeSessionProvider")
public class KnowledgeSessionProvider implements Serializable {
    
    @Autowired
	private KnowledgeBaseProducer knowledgeBaseProducer;
    
    public KnowledgeBaseProducer getKnowledgeBaseProducer() {
		return knowledgeBaseProducer;
	}
    
    public void upgradeProcessInstance(String urlFile, String processId, String nomProjet) {
    	long processInstanceId_ = processInstances.get(nomProjet).getId();
    	// add a new version of the process "com.sample.process2"
    	knowledgeBaseProducer.addResource(urlFile);
    	
    	ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(idKsession, 
				knowledgeBaseProducer.getKbase(), getConfig(), getEnv());
    	

    	// migrate process instance to new version
    	Map<String, Long> mapping = new HashMap<String, Long>();

    	// top level node 2 is mapped to a new node with id 3
    	//mapping.put("2", 3L); 

    	// node 2, which is part of composite node 5, is mapped to a new node with id 4
    	//mapping.put("5.2", 4L); 

    	//"com.tnt.bpmn.projectProcess"
    	//ksession.createProcessInstance(processId, null);
//    	WorkflowProcessInstanceUpgrader.upgradeProcessInstance(
//
//    			ksession, processInstanceId_,
//
//    			processId, null);
    }

	private StatefulKnowledgeSession ksession;
	private int idKsession;
    
    @Inject @Named("entityManagerFactoryJbpm")
	private EntityManagerFactory emf;
    
    @Inject @Named("jbpmTxManager")
    JtaTransactionManager jbpmTxManager;
    
    private Map<String, ProcessInstance> processInstances = new HashMap<String, ProcessInstance>();
    

	public ProcessInstance get(String key) {
		return processInstances.get(key);
	}

	public ProcessInstance put(String key, ProcessInstance v) {
		return processInstances.put(key, v);
	}

	public StatefulKnowledgeSession getKsession() {
		if (ksession == null) {
			createKnowledgeSession();
		}
		return ksession;
	}
	
	public StatefulKnowledgeSession loadKsession() {
		ksession = JPAKnowledgeService.loadStatefulKnowledgeSession(idKsession, 
				knowledgeBaseProducer.getKbase(), getConfig(), getEnv());
		return ksession;
	}

	private void createKnowledgeSession() {

		Environment env = getEnv();

		KnowledgeSessionConfiguration config = getConfig();

		ksession = JPAKnowledgeService.newStatefulKnowledgeSession(knowledgeBaseProducer.produceKnowledgeBase(), config, env);
		idKsession = ksession.getId();
	}

	private Environment getEnv() {
		Environment env = KnowledgeBaseFactory.newEnvironment();

		env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, emf );
		//env.set( EnvironmentName.TRANSACTION_MANAGER, TransactionManagerServices.getTransactionManager() );
		//env.set( EnvironmentName.TRANSACTION_MANAGER, getTransactionManager() );
		env.set( EnvironmentName.TRANSACTION_MANAGER, jbpmTxManager.getTransactionManager() );
		
		env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );
		return env;
	}

	private KnowledgeSessionConfiguration getConfig() {
		Properties properties = new Properties();
		properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
		properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");

		KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
		return config;
	}
	
	/*private TransactionManager getTransactionManager()   {
        TransactionManager tm  = null;
        try {
        InitialContext ic = new InitialContext();      
        tm  = (TransactionManager) ic.lookup("java:jboss/TransactionManager");
        if (tm == null) throw new IllegalStateException("Couldn't find the transaction mamager in JNDI");      
        }catch (Exception exc) {
            exc.printStackTrace();
        }
        return tm;  
    }*/
	
	public void setKnowledgeBaseProducer(KnowledgeBaseProducer knowledgeBaseProducer) {
		this.knowledgeBaseProducer = knowledgeBaseProducer;
	}
}

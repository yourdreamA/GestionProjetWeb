package com.tnt.gestionproj.jbpm.server;


import java.io.IOException;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.TransactionManager;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.impl.EnvironmentFactory;
import org.drools.io.ResourceFactory;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
import org.jbpm.process.workitem.wsht.WSHumanTaskHandler;

//@WebServlet("/StartProcess")
public class StartProcess extends HttpServlet {
   private static final long serialVersionUID = 1L;
   StatefulKnowledgeSession ksession = null;
   KnowledgeBase kbase = null;
   public StartProcess() {
       super();
       // TODO Auto-generated constructor stub
    }

   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       try {
        /*
        * Reads the KnowledgeBase
        */
           kbase = readKnowledgeBase();
       } catch (Exception e) {
           
           e.printStackTrace();
       }
       EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
       
       /*
        * Creates the knowledge session that uses JPA to persists runtime state
        */
       StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        /*
        * Registers the Human Task Service in the jBPM engine
        */
       ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new WSHumanTaskHandler());
       ksession.startProcess("com.sample.bpmn.hello");
       System.out.println("Process started successfully...");
   }
   
    private static StatefulKnowledgeSession createKnowledgeSession(KnowledgeBase kbase) {

           EntityManagerFactory emf = Persistence.createEntityManagerFactory( "org.jbpm.persistence.jpa" );

           Environment env = KnowledgeBaseFactory.newEnvironment();

           env.set( EnvironmentName.ENTITY_MANAGER_FACTORY, emf );
           env.set( EnvironmentName.TRANSACTION_MANAGER, getTransactionManager() );
           env.set( EnvironmentName.GLOBALS, new MapGlobalResolver() );

           Properties properties = new Properties();
           properties.put("drools.processInstanceManagerFactory", "org.jbpm.persistence.processinstance.JPAProcessInstanceManagerFactory");
           properties.put("drools.processSignalManagerFactory", "org.jbpm.persistence.processinstance.JPASignalManagerFactory");

           KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);

           return JPAKnowledgeService.newStatefulKnowledgeSession(kbase, config, env);

   
       }

     
   private static KnowledgeBase readKnowledgeBase() throws Exception {
       KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
       kbuilder.add(ResourceFactory.newClassPathResource("sample.bpmn"), ResourceType.BPMN2);
       return kbuilder.newKnowledgeBase();
   }


   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {   
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

package com.tnt.gestionproj.test;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.drools.KnowledgeBase;
import org.drools.SystemEventListenerFactory;
import org.drools.audit.WorkingMemoryInMemoryLogger;
import org.drools.audit.event.LogEvent;
import org.drools.audit.event.RuleFlowNodeLogEvent;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.process.Node;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.NodeInstance;
import org.drools.runtime.process.NodeInstanceContainer;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.NodeInstanceLog;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.TaskService;
import org.jbpm.task.identity.DefaultUserGroupCallbackImpl;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.test.KnowledgeSessionCleanup;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bitronix.tm.resource.jdbc.PoolingDataSource;

/**
 * Base test case for the jbpm-bpmn2 module. 
 *
 * Please keep this test class in the org.jbpm.bpmn2 package or otherwise give it a unique name. 
 *
 */
public abstract class JbpmGestionProjJUnitTestCase extends Assert {
	
    protected final static String EOL = System.getProperty( "line.separator" );
    
    private boolean setupDataSource = false;
	private boolean sessionPersistence = false;
	private EntityManagerFactory emf;
	private PoolingDataSource ds;
	private org.jbpm.task.service.TaskService taskService;

	private TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
	
	private WorkingMemoryInMemoryLogger logger;
	private JPAProcessInstanceDbLog log;
	private Logger testLogger = null;

    @Rule
    public KnowledgeSessionCleanup ksessionCleanupRule = new KnowledgeSessionCleanup();	
   
	@Rule
	public TestName testName = new TestName();
    
    public void setPersistence(boolean sessionPersistence) {
    	this.sessionPersistence = sessionPersistence;
    }
	
	public boolean isPersistence() {
		return sessionPersistence;
	}

    
    protected KnowledgeBase createKnowledgeBase(String... process) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (String p: process) {
            kbuilder.add(ResourceFactory.newClassPathResource(p), ResourceType.BPMN2);
        }
        
        // Check for errors
        if (kbuilder.hasErrors()) {
            if (kbuilder.getErrors().size() > 0) {
                boolean errors = false;
                for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                    testLogger.error(error.toString());
                    errors = true;
                }
                assertFalse("Could not build knowldge base.", errors);
            }
        }
        return kbuilder.newKnowledgeBase();
    }
	
	protected KnowledgeBase createKnowledgeBase(Map<String, ResourceType> resources) throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		for (Map.Entry<String, ResourceType> entry: resources.entrySet()) {
			kbuilder.add(ResourceFactory.newClassPathResource(entry.getKey()), entry.getValue());
		}
		return kbuilder.newKnowledgeBase();
	}
	
	protected KnowledgeBase createKnowledgeBaseGuvnor(String... packages) throws Exception {
		return createKnowledgeBaseGuvnor(false, "http://localhost:8080/drools-guvnor", "admin", "admin", packages);
	}
	
	protected KnowledgeBase createKnowledgeBaseGuvnorAssets(String pkg, String...assets) throws Exception {
	    return createKnowledgeBaseGuvnor(false, "http://localhost:8080/drools-guvnor", "admin", "admin", pkg, assets);
	}
	
	protected KnowledgeBase createKnowledgeBaseGuvnor(boolean dynamic, String url, String username, 
            String password, String pkg, String... assets) throws Exception {
	    KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
	    String changeSet = 
	        "<change-set xmlns='http://drools.org/drools-5.0/change-set'" + EOL +
	        "            xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'" + EOL +
	        "            xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >" + EOL +
	        "    <add>" + EOL;
	    for(String a : assets) {
	        if(a.indexOf(".bpmn") >= 0) {
	            a = a.substring(0, a.indexOf(".bpmn"));
	        } 
	        changeSet += "        <resource source='" + url + "/rest/packages/" + pkg + "/assets/" + a + "/binary' type='BPMN2' basicAuthentication=\"enabled\" username=\"" + username + "\" password=\"" + password + "\" />" + EOL;
	    }
	    changeSet +=
	        "    </add>" + EOL +
	        "</change-set>";
	    kbuilder.add(ResourceFactory.newByteArrayResource(changeSet.getBytes()), ResourceType.CHANGE_SET);
	    return kbuilder.newKnowledgeBase();
	}

	protected KnowledgeBase createKnowledgeBaseGuvnor(boolean dynamic, String url, String username, 
													  String password, String... packages) throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		String changeSet = 
			"<change-set xmlns='http://drools.org/drools-5.0/change-set'" + EOL +
			"            xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'" + EOL +
			"            xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >" + EOL +
			"    <add>" + EOL;
		for (String p : packages) {
			changeSet += "        <resource source='" + url + "/rest/packages/" + p + "/binary' type='PKG' basicAuthentication=\"enabled\" username=\"" + username + "\" password=\"" + password + "\" />" + EOL;
		}
		changeSet +=
			"    </add>" + EOL +
			"</change-set>";
		kbuilder.add(ResourceFactory.newByteArrayResource(changeSet.getBytes()), ResourceType.CHANGE_SET);
		return kbuilder.newKnowledgeBase();
	}
	
	
	public Object getVariableValue(String name, long processInstanceId, StatefulKnowledgeSession ksession) {
		return ((WorkflowProcessInstance) ksession.getProcessInstance(processInstanceId)).getVariable(name);
	}
    
	public void assertProcessInstanceCompleted(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNull(ksession.getProcessInstance(processInstanceId));
	}
	
	public void assertProcessInstanceAborted(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNull(ksession.getProcessInstance(processInstanceId));
	}
	
	public void assertProcessInstanceActive(long processInstanceId, StatefulKnowledgeSession ksession) {
		assertNotNull(ksession.getProcessInstance(processInstanceId));
	}
	
	public void assertNodeActive(long processInstanceId, StatefulKnowledgeSession ksession, String... name) {
		List<String> names = new ArrayList<String>();
		for (String n: name) {
			names.add(n);
		}
		ProcessInstance processInstance = ksession.getProcessInstance(processInstanceId);
		if (processInstance instanceof WorkflowProcessInstance) {
			assertNodeActive((WorkflowProcessInstance) processInstance, names);
		}
		if (!names.isEmpty()) {
			String s = names.get(0);
			for (int i = 1; i < names.size(); i++) {
				s += ", " + names.get(i);
			}
			fail("Node(s) not active: " + s);
		}
	}
	
	private void assertNodeActive(NodeInstanceContainer container, List<String> names) {
		for (NodeInstance nodeInstance: container.getNodeInstances()) {
			String nodeName = nodeInstance.getNodeName();
			if (names.contains(nodeName)) {
				names.remove(nodeName);
			}
			if (nodeInstance instanceof NodeInstanceContainer) {
				assertNodeActive((NodeInstanceContainer) nodeInstance, names);
			}
		}
	}
	
	public void assertNodeTriggered(long processInstanceId, String... nodeNames) {
		List<String> names = new ArrayList<String>();
		for (String nodeName: nodeNames) {
			names.add(nodeName);
		}
		if (sessionPersistence) {
			List<NodeInstanceLog> logs = log.findNodeInstances(processInstanceId);
			if (logs != null) {
				for (NodeInstanceLog l: logs) {
					String nodeName = l.getNodeName();
					if ((l.getType() == NodeInstanceLog.TYPE_ENTER || l.getType() == NodeInstanceLog.TYPE_EXIT) && names.contains(nodeName)) {
						names.remove(nodeName);
					}
				}
			}
		} else {
			for (LogEvent event: logger.getLogEvents()) {
				if (event instanceof RuleFlowNodeLogEvent) {
					String nodeName = ((RuleFlowNodeLogEvent) event).getNodeName();
					if (names.contains(nodeName)) {
						names.remove(nodeName);
					}
				}
			}
		}
		if (!names.isEmpty()) {
			String s = names.get(0);
			for (int i = 1; i < names.size(); i++) {
				s += ", " + names.get(i);
			}
			fail("Node(s) not executed: " + s);
		}
	}
	
	protected void clearHistory() {
		if (sessionPersistence) {
			if (log == null) {
				log = new JPAProcessInstanceDbLog();
			}
			log.clear();
		} else {
			logger.clear();
		}
	}
	
	public TestWorkItemHandler getTestWorkItemHandler() {
		return workItemHandler;
	}
	
	public static class TestWorkItemHandler implements WorkItemHandler {
		
	    private List<WorkItem> workItems = new ArrayList<WorkItem>();
	    
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            workItems.add(workItem);
        }
        
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        }
        
        public WorkItem getWorkItem() {
        	if (workItems.size() == 0) {
        		return null;
        	}
        	if (workItems.size() == 1) {
        		WorkItem result = workItems.get(0);
        		this.workItems.clear();
        		return result;
        	} else {
        		throw new IllegalArgumentException("More than one work item active");
        	}
        }
        
        public List<WorkItem> getWorkItems() {
        	List<WorkItem> result = new ArrayList<WorkItem>(workItems);
        	workItems.clear();
        	return result;
        }
        
	}
	
	public void assertProcessVarExists(ProcessInstance process, String... processVarNames) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        List<String> names = new ArrayList<String>();
        for (String nodeName: processVarNames) {
            names.add(nodeName);
        }
        
        for(String pvar : instance.getVariables().keySet()) {
            if (names.contains(pvar)) {
                names.remove(pvar);
            }
        }
        
        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Process Variable(s) do not exist: " + s);
        }

    }
    
    public void assertNodeExists(ProcessInstance process, String... nodeNames) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        List<String> names = new ArrayList<String>();
        for (String nodeName: nodeNames) {
            names.add(nodeName);
        }
        
        for(Node node : instance.getNodeContainer().getNodes()) {
            if (names.contains(node.getName())) {
                names.remove(node.getName());
            }
        }
        
        if (!names.isEmpty()) {
            String s = names.get(0);
            for (int i = 1; i < names.size(); i++) {
                s += ", " + names.get(i);
            }
            fail("Node(s) do not exist: " + s);
        }
    }
    
    public void assertNumOfIncommingConnections(ProcessInstance process, String nodeName, int num) {
        assertNodeExists(process, nodeName);
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        for(Node node : instance.getNodeContainer().getNodes()) {
            if(node.getName().equals(nodeName)) {
                if(node.getIncomingConnections().size() != num) {
                    fail("Expected incomming connections: " + num + " - found " + node.getIncomingConnections().size());
                } else {
                    break;
                }
            }
        }
    }
    
    public void assertNumOfOutgoingConnections(ProcessInstance process, String nodeName, int num) {
        assertNodeExists(process, nodeName);
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        for(Node node : instance.getNodeContainer().getNodes()) {
            if(node.getName().equals(nodeName)) {
                if(node.getOutgoingConnections().size() != num) {
                    fail("Expected outgoing connections: " + num + " - found " + node.getOutgoingConnections().size());
                } else {
                    break;
                }
            }
        }
    }
    
    public void assertVersionEquals(ProcessInstance process, String version) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if(!instance.getWorkflowProcess().getVersion().equals(version)) {
            fail("Expected version: " + version + " - found " + instance.getWorkflowProcess().getVersion());
        }
    }
    
    public void assertProcessNameEquals(ProcessInstance process, String name) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if(!instance.getWorkflowProcess().getName().equals(name)) {
            fail("Expected name: " + name + " - found " + instance.getWorkflowProcess().getName());
        }
    }
    
    public void assertPackageNameEquals(ProcessInstance process, String packageName) {
        WorkflowProcessInstanceImpl instance = (WorkflowProcessInstanceImpl) process;
        if(!instance.getWorkflowProcess().getPackageName().equals(packageName)) {
            fail("Expected package name: " + packageName + " - found " + instance.getWorkflowProcess().getPackageName());
        }
    }
    
    public TaskService getTaskService(StatefulKnowledgeSession ksession) {
    	if (taskService == null) {
    		taskService = new org.jbpm.task.service.TaskService(
				emf, SystemEventListenerFactory.getSystemEventListener());
    		
    		UserGroupCallbackManager.getInstance().setCallback(new DefaultUserGroupCallbackImpl("classpath:/usergroups.properties"));
    	}
    	LocalTaskService localTaskService = new LocalTaskService(taskService);
		LocalHTWorkItemHandler humanTaskHandler = new LocalHTWorkItemHandler(
			localTaskService, ksession);
		humanTaskHandler.connect();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", humanTaskHandler);
		return localTaskService;
    }
    
    public org.jbpm.task.service.TaskService getService() {
		return new org.jbpm.task.service.TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
	}
    
}


package com.tnt.gestionproj.jbpm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.SystemEventListenerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.Group;
import org.jbpm.task.TaskService;
import org.jbpm.task.User;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.task.utils.OnErrorAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tnt.gestionproj.service.UserService;
import com.tnt.gestionproj.vo.LoginVO;

@Component(value="knowledgeTaskProvider")
public class KnowledgeTaskProvider {

	private TaskService taskService;
	
	@Autowired
	private KnowledgeSessionProvider ksessionProvider;
	
	@Autowired
	private UserService userService;
	
	private LocalHTWorkItemHandler localHTWorkItemHandler;
	
//	@Inject @Named("entityManagerFactoryJbpm")
//	private EntityManagerFactory emf;
	
	public LocalHTWorkItemHandler getLocalHTWorkItemHandler() {
		if (localHTWorkItemHandler == null) {
			try {
				getTaskService();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return localHTWorkItemHandler;
	}

	@Autowired
	KnowledgeTaskServiceSession taskServiceSession;
	
public TaskService getTaskService()throws Exception {
		if (taskService == null) {
			//EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
			StatefulKnowledgeSession ksession = ksessionProvider.getKsession();
			
			org.jbpm.task.service.TaskService tservice = getService();
			
			taskService = getTskService(ksession,tservice);
		}
		
		return taskService;
	}

	public org.jbpm.task.service.TaskService getService() {
		return taskServiceSession.getTaskService();
	}
	
	public org.jbpm.task.TaskService getTskService(StatefulKnowledgeSession ksession,org.jbpm.task.service.TaskService taskService) {

	    taskService =  addUsersandGroups(taskService);
		org.jbpm.task.TaskService client = new LocalTaskService(taskService);

        localHTWorkItemHandler = new LocalHTWorkItemHandler(client, ksession,OnErrorAction.RETHROW);
	        localHTWorkItemHandler.connect();
	        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", localHTWorkItemHandler);
	        return client;		
    }
	
	public org.jbpm.task.service.TaskService addUsersandGroups(org.jbpm.task.service.TaskService taskService) {
		Map<String, User> users = new HashMap<String, User>();
	    
	    List<LoginVO> list = userService.getAllLogin();
	       for (LoginVO loginVO : list) {
	    	   users.put(loginVO.getUsername(), new User(loginVO.getUsername()));
	       }
	       users.put("Administrator", new User("Administrator"));
	   
	    Map<String, Group> groups = new HashMap<String, Group>();
	    taskService.addUsersAndGroups(users, groups);
	    
	    return taskService;
	}
}

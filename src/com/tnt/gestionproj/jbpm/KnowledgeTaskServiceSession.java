package com.tnt.gestionproj.jbpm;

import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManagerFactory;

import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.jbpm.task.User;
import org.jbpm.task.service.DefaultEscalatedDeadlineHandler;
import org.jbpm.task.service.DefaultUserInfo;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tnt.gestionproj.service.UserService;
import com.tnt.gestionproj.vo.UserVO;

@Component(value="knowledgeTaskServiceSession")
public class KnowledgeTaskServiceSession {

	@Autowired
	private UserService userService;
	
	@Inject @Named("entityManagerFactoryJbpm")
	private EntityManagerFactory emfTask;
	
	private TaskService taskService;
	private TaskServiceSession taskSession;

	public TaskService getTaskService() {
		if (taskService == null) {
			initTaskService();
		}
		return taskService;
	}
	
	private void initTaskService() {
		
		List<UserVO> list = userService.getAllUser();

		Environment env = KnowledgeBaseFactory.newEnvironment();
		env.set(EnvironmentName.ENTITY_MANAGER_FACTORY, emfTask);
		env.set(EnvironmentName.GLOBALS, new MapGlobalResolver());

//		Properties emailProperties = getEmailProperties();

		// build escalation handler
//		DefaultEscalatedDeadlineHandler handler = new DefaultEscalatedDeadlineHandler(emailProperties);

		// configure default UserInfo
//		Properties userInfoProperties = getUserInfoProperties(list);

		// set user info on the escalation handler
//		handler.setUserInfo(new DefaultUserInfo(userInfoProperties));
		
		taskService = new TaskService(emfTask,
				SystemEventListenerFactory.getSystemEventListener()//, handler
				);

		taskSession = taskService.createSession();
		
		// charger list login
		if (list != null && !list.isEmpty()) {
			for (UserVO userVO : list) {
				taskSession.addUser(new User(userVO.getLogin().getUsername()));
			}
		}
		taskSession.addUser(new User("Administrator"));
	}
	
	/**
	 * configure default UserInfo
	 * @param list
	 * @return
	 */
	private Properties getUserInfoProperties(List<UserVO> list) {
		Properties userInfoProperties = new Properties();

		if (list != null && !list.isEmpty()) {
			for (UserVO userVO : list) {
				// : separated values for each org entity email:locale:display-name
				userInfoProperties.setProperty(userVO.getLogin().getUsername(),
						userVO.getEmail() + ":en-UK:" + userVO.getPrenom() + userVO.getNom());
			}
		}

		return userInfoProperties;
	}

	private Properties getEmailProperties() {
		Properties emailProperties = new Properties();
		emailProperties.setProperty("from", "abbassi.abderrahmen@gmail.com");
		emailProperties.setProperty("replyTo", "abbassi.abderrahmen@gmail.com");
		emailProperties.setProperty("mail.smtp.host", "smtp.gmail.com");
		emailProperties.setProperty("mail.smtp.port", "587");
		return emailProperties;
	}
	
}

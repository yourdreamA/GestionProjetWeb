package com.tnt.gestionproj.app.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.process.workitem.email.EmailWorkItemHandler;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tnt.gestionproj.jbpm.KnowledgeSessionProvider;
import com.tnt.gestionproj.jbpm.KnowledgeTaskProvider;
import com.tnt.gestionproj.jbpm.workitem.SpringInvokeWorkItemHandler;
import com.tnt.gestionproj.service.UserService;
import com.tnt.gestionproj.vo.LoginVO;
import com.tnt.gestionproj.vo.UserVO;

@Component("userApp")
public class UserAppImpl {

	@Autowired
	private UserService userService;

	@Autowired
	private KnowledgeSessionProvider ksessionProvider;
	
	@Autowired
	private KnowledgeTaskProvider taskProvider;

	public UserVO login(LoginVO login) throws Exception {

		UserVO userVO = userService.login(login);
		
		StatefulKnowledgeSession ksession = ksessionProvider.getKsession();
		
		TaskService taskService = taskProvider.getTaskService();
		
		List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(login.getUsername(), "en-UK");
		List<TaskSummary> tasks2 = taskService.getTasksOwned(login.getUsername(), "en-UK");
		
		return userVO;
	}

	//test existence user
	public String addUser(UserVO user) throws Exception {
		String res = "";
		
		//TODO controle login exist
		if (userService.existsUser(user.getLogin().getUsername())) {
			throw new Exception("Utilisateur existe déjà.");
		}
		
		// generate login and pwd
		//user.getLogin().setPassword(userService.generatePwd());
		
		
		// save user
		//userService.addUser(user);
		
		
		StatefulKnowledgeSession ksession = ksessionProvider.getKsession();

		// generate login and pwd
		SpringInvokeWorkItemHandler springInvoke = new SpringInvokeWorkItemHandler(
				"com.tnt.gestionproj.service.UserService", userService);
		
		ksession.getWorkItemManager().registerWorkItemHandler("Spring Invoke", springInvoke);

		// send mail to user
		EmailWorkItemHandler emailHandler = new EmailWorkItemHandler();
		emailHandler.setConnection("smtp.gmail.com", "587",
				"abbassi.abderrahmen@gmail.com", "abdb2013");
		emailHandler.getConnection().setStartTls(true);
		
		ksession.getWorkItemManager().registerWorkItemHandler("Email WID",
				emailHandler);
		
		// save user
		ksession.getWorkItemManager().registerWorkItemHandler("Spring Invoke", springInvoke);
		
		
		Map<String, Object> params = new HashMap<String, Object>();
		 params.put("user", user);
		//mail
		 params.put("From", "abbassi.abderrahmen@gmail.com");
		 params.put("To", user.getEmail());
		 params.put("Subject", "Nouveau compte utilisateur");
		 params.put("Body", "login : " + user.getLogin().getUsername() + "<br>pwd : ");
		 
		 params.put("Subject", "Nouveau compte utilisateur");
		 
		 
		 
		ProcessInstance instance = ksession.startProcess("com.tnt.bpmn.CreateUser",
				params);
		
		RuleFlowProcessInstance r = (RuleFlowProcessInstance) instance;
		res = "L'utilisateur " + user.getLogin().getUsername() + " est ajouté avec succès.\n";
		res += "Le mot de passe généré est envoyé par mail à l'adresse " + user.getEmail();
		return res;
	}
	
}

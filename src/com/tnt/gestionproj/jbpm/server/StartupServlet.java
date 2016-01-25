package com.tnt.gestionproj.jbpm.server;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.KnowledgeBaseFactory;
import org.drools.SystemEventListenerFactory;
import org.drools.base.MapGlobalResolver;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.jbpm.task.User;
import org.jbpm.task.service.DefaultEscalatedDeadlineHandler;
import org.jbpm.task.service.DefaultUserInfo;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.tnt.gestionproj.jbpm.KnowledgeSessionProvider;
import com.tnt.gestionproj.jbpm.KnowledgeTaskServiceSession;
import com.tnt.gestionproj.service.UserService;
import com.tnt.gestionproj.vo.LoginVO;
import com.tnt.gestionproj.vo.UserVO;

/**
 * Servlet implementation class StartupServlet
 */
@WebServlet(value = "/StartupServlet", loadOnStartup = 1)
public class StartupServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig config) throws ServletException {

		WebApplicationContext ctx = getSpringCtx(config.getServletContext());
		KnowledgeTaskServiceSession taskServiceSession = (KnowledgeTaskServiceSession) ctx.getBean("knowledgeTaskServiceSession");
		

		MinaTaskServer server = new MinaTaskServer(taskServiceSession.getTaskService());
		Thread thread = new Thread(server);
		thread.start();

		// TODO charger bpmn
		KnowledgeSessionProvider provider = (KnowledgeSessionProvider) ctx
				.getBean(KnowledgeSessionProvider.class);
		// Collection<org.drools.runtime.process.ProcessInstance> listProcess =
		// provider.getKsession().getProcessInstances();
		// JPAKnowledgeService.loadStatefulKnowledgeSession(id, kbase,
		// configuration, environment);
		// provider.createKnowledgeSession();
		// provider.getKsession().getWorkItemManager().registerWorkItemHandler("Human Task",
		// new WSHumanTaskHandler(provider.getKsession()));
		// provider.getKsession().startProcess("com.tnt.bpmn.projectProcess");
		// provider.getKsession().startProcess("com.tnt.bpmn.taskProcess");
		//
		// System.out.println("Started task service!");
	}

	private WebApplicationContext getSpringCtx(ServletContext servletContext) {
		WebApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(servletContext);

		return ctx;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

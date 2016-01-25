package com.tnt.gestionproj.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.task.Status;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tnt.gestionproj.app.impl.ProjetAppImpl;
import com.tnt.gestionproj.app.impl.SousProjetAppImpl;
import com.tnt.gestionproj.jbpm.KnowledgeSessionProvider;
import com.tnt.gestionproj.jbpm.KnowledgeTaskProvider;
import com.tnt.gestionproj.jbpm.KnowledgeTaskServiceSession;
import com.tnt.gestionproj.service.SousProjetService;
import com.tnt.gestionproj.service.UserService;
import com.tnt.gestionproj.vo.AdministrateurVO;
import com.tnt.gestionproj.vo.ChefProjetVO;
import com.tnt.gestionproj.vo.ChefSousProjetVO;
import com.tnt.gestionproj.vo.ExecuteurVO;
import com.tnt.gestionproj.vo.LoginVO;
import com.tnt.gestionproj.vo.ProjetVO;
import com.tnt.gestionproj.vo.SousProjetVO;
import com.tnt.gestionproj.vo.TacheVO;
import com.tnt.gestionproj.vo.UserVO;

/**
 * This is a sample file to launch a process.
 */

public class ProcessTest extends JbpmGestionProjJUnitTestCase {
	
	String nomProjet = "t_p_127";

	private static ClassPathXmlApplicationContext context;

	private static ProjetAppImpl projetAppImpl;
	private static UserService userService;
	
	private static KnowledgeSessionProvider ksessionProvider;
	private static KnowledgeTaskProvider taskProvider;
	
	
	private static SousProjetAppImpl sousProjetAppImpl;
	private static SousProjetService sousProjetService;
	
	
	private static List<Status> status = new ArrayList<Status>();
	
	@BeforeClass
	public static void setUp2() throws Exception {
		System.setProperty("java.naming.factory.initial",
				"bitronix.tm.jndi.BitronixInitialContextFactory");
		
		context = new ClassPathXmlApplicationContext("applicationContext_test.xml");
		
		status.add(Status.Created);
		status.add(Status.Reserved);
		
		projetAppImpl = (ProjetAppImpl) context.getBean("projetApp");
		sousProjetAppImpl = (SousProjetAppImpl) context.getBean("sousProjetApp");
		userService = (UserService) context.getBean("userService");
		ksessionProvider = (KnowledgeSessionProvider) context.getBean("knowledgeSessionProvider");
		taskProvider = (KnowledgeTaskProvider) context.getBean("knowledgeTaskProvider");
		sousProjetService = (SousProjetService) context.getBean("sousProjetService");
		
		//Console log. Try to analyze it first
        //KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksessionProvider.getKsession());
        
		KnowledgeTaskServiceSession taskServiceSession = (KnowledgeTaskServiceSession) context.getBean("knowledgeTaskServiceSession");
		MinaTaskServer server = new MinaTaskServer(taskServiceSession.getTaskService());
		Thread thread = new Thread(server);
		thread.start();
	}
	
	@Test
	public void testProjectProcess() {
		//1- create project by admin
		AdministrateurVO admin = getAdministrateur();
		ChefProjetVO chefProj = getChefProjet();
		
		
		ProjetVO projet = addProjet(nomProjet, admin, chefProj);
		//ProcessInstance processInstance = ksessionProvider.get(nomProjet);
		StatefulKnowledgeSession ksession = ksessionProvider.getKsession();
		//Tests :
		//assertProcessInstanceActive(processInstance.getId(), ksession );
		
		//test node
//		assertNodeTriggered(processInstance.getId(), "Start", "Add project", "Select user", "Affect project", "Validate add project");
		
		//test task chef projet
		try {
			String username = chefProj.getLogin().getUsername();
			List<TaskSummary> tasks = taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(
					username, status, "en-UK");
				
			assertTrue("Pas de tache affecté à " + username, tasks != null && !tasks.isEmpty());
			
			TaskSummary task = tasks.get(0);
			System.out.println(username + " is executing task " + task.getName());
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		
		
		//2- structure project by chef project
		List<SousProjetVO> listSousProjet = structureProjet(projet);
		//Tests :
		//test node
//		assertNodeTriggered(processInstance.getId(), "", "Generate structure project");
		//test task of chef sous projet (list : for)
		ExecuteurVO executeur1 = getExecuteur1();
		ExecuteurVO executeur2 = getExecuteur2();
		for (SousProjetVO sousProjetVO : listSousProjet) {
			String _username = sousProjetVO.getChefSousProjet().getLogin().getUsername();
			try {
				List<TaskSummary> tasks = taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(
						_username, status, "en-UK");
				if (tasks != null && !tasks.isEmpty()) {
					TaskSummary task = tasks.get(0);
					System.out.println(_username + " has task " + task.getName());
					
					try {
						SousProjetVO sp = getSousProjet(sousProjetVO.getNom(), sousProjetVO.getChefSousProjet(), projet);
						sousProjetAppImpl.addTask(sp, getTache(sp, "1", executeur1));
						sousProjetAppImpl.addTask(sp, getTache(sp, "2", executeur2));
						sousProjetAppImpl.launchSubProject(sp);
					} catch (Exception e) {
						e.printStackTrace();
						assertTrue(false);
					}
					
					
				} else {
					System.out.println("No task for " + _username);
				}
				
				String usernameExec1 = executeur1.getLogin().getUsername();
				List<TaskSummary> tasksExec1 = taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(
						_username, status, "en-UK");
				if (tasksExec1 != null && !tasksExec1.isEmpty()) {
					TaskSummary task = tasksExec1.get(0);
					System.out.println("+++++++++++++" + usernameExec1 + " has task " + task.getName());
				} else {
					System.out.println("+++++++++++++No task for " + usernameExec1);
				}
				
				String usernameExec2 = executeur2.getLogin().getUsername();
				List<TaskSummary> tasksExec2 = taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(
						_username, status, "en-UK");
				if (tasksExec2 != null && !tasksExec2.isEmpty()) {
					TaskSummary task = tasksExec2.get(0);
					System.out.println("+++++++++++++" + usernameExec2 + " has task " + task.getName());
				} else {
					System.out.println("+++++++++++++No task for " + usernameExec2);
				}
				
			} catch (Exception e) {
				assertTrue(false);
				e.printStackTrace();
			}
		}
		//3 - for sous projet
		// structure subproject by chef sous projet
		//Tests :
		//test node
		//test task executeur
		//4- release task by executeur
		//5- validate end task
		//6- validate end sub projetc
		
		//7- validate end project
		
		//ksession.dispose();
	}

	//@Test
	/*public void testProcess() {
		StatefulKnowledgeSession ksession = createKnowledgeSession("sample.bpmn");
		TaskService taskService = getTaskService(ksession);
		
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");

		assertProcessInstanceActive(processInstance.getId(), ksession);
		assertNodeTriggered(processInstance.getId(), "Start", "Task 1");
		
		// let john execute Task 1
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		TaskSummary task = list.get(0);
		System.out.println("John is executing task " + task.getName());
		taskService.start(task.getId(), "john");
		taskService.complete(task.getId(), "john", null);

		assertNodeTriggered(processInstance.getId(), "Task 2");
		
		// let mary execute Task 2
		list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		task = list.get(0);
		System.out.println("Mary is executing task " + task.getName());
		taskService.start(task.getId(), "mary");
		taskService.complete(task.getId(), "mary", null);

		assertNodeTriggered(processInstance.getId(), "End");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
		
		ksession.dispose();
	}*/
	
	/**
	 * 111111111111111111111111111111111111111111111111111111111111111111111
	 * @param nomProjet
	 * @param admin
	 * @param chefProj
	 */
	private ProjetVO addProjet(String nomProjet, AdministrateurVO admin, ChefProjetVO chefProj) {
		ProjetVO projet = new ProjetVO();
		projet.setNom(nomProjet);
		projet.setAdministrateur(admin);
		projet.setChefProjet(chefProj);
		projet.setDateDebut(getDate(2013, 6, 20));
		projet.setDateFin(getDate(2013, 11, 31));
		
		

		try {
			
			projet = projetAppImpl.addProjet(projet);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
		return projet;
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	private AdministrateurVO getAdministrateur() {
		return (AdministrateurVO) searchUserByLogin("aba", "1000");
	}
	
	private ChefProjetVO getChefProjet() {
		return (ChefProjetVO) searchUserByLogin("aba_ch", "1000");
	}
	
	private ChefSousProjetVO getChefSousProjet1() {
		return (ChefSousProjetVO) searchUserByLogin("aba_sch", "1000");
	}
	
	private ChefSousProjetVO getChefSousProjet2() {
		return (ChefSousProjetVO) searchUserByLogin("aba_sch_2", "1000");
	}
	
	private ExecuteurVO getExecuteur1() {
		return (ExecuteurVO) searchUserByLogin("aba_ex", "1000");
	}
	
	private ExecuteurVO getExecuteur2() {
		return (ExecuteurVO) searchUserByLogin("aba_ex2", "1000");
	}
	
	private UserVO searchUserByLogin(String username, String password) {
		LoginVO login = new LoginVO();
		login.setUsername(username);
		login.setPassword(password);
		
		return userService.searchUserByLogin(login);
	}
	
	private Calendar getDate(int y, int m, int d) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, y);
		cal.set(Calendar.MONTH, m);
		cal.set(Calendar.DATE, d);
		String dateS = df.format(cal.getTime());
		
		try {
			cal.setTime(df.parse(dateS));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		return cal;
	}
	
	/**
	 * 2222222222222222222222222222222222222222222222222222222222222222222222
	 * @param idProjet 
	 */
	private List<SousProjetVO> structureProjet(ProjetVO projet) {
		
		List<SousProjetVO> listSousProjet = new ArrayList<SousProjetVO>();
		
		SousProjetVO sp1 = new SousProjetVO();
		sp1.setNom("sp 1");
		sp1.setDateDebut(getDate(2014, 1, 1));
		sp1.setDateFin(getDate(2013, 2, 31));
		sp1.setChefSousProjet(getChefSousProjet1());
		sp1.setProjet(projet);
		
		
		SousProjetVO sp2 = new SousProjetVO();
		sp2.setNom("sp 2");
		sp2.setDateDebut(getDate(2014, 3, 1));
		sp2.setDateFin(getDate(2013, 4, 31));
		sp2.setChefSousProjet(getChefSousProjet2());
		sp2.setProjet(projet);
		sp1.setSousProjetSuivant(sp2);
		
		SousProjetVO sp3 = new SousProjetVO();
		sp3.setNom("sp 3");
		sp3.setDateDebut(getDate(2014, 5, 1));
		sp3.setDateFin(getDate(2013, 6, 30));
		sp3.setChefSousProjet(getChefSousProjet1());
		sp3.setProjet(projet);
		sp2.setSousProjetSuivant(sp3);
		
		SousProjetVO sp4 = new SousProjetVO();
		sp4.setNom("sp 4");
		sp4.setDateDebut(getDate(2014, 7, 1));
		sp4.setDateFin(getDate(2013, 8, 30));
		sp4.setChefSousProjet(getChefSousProjet2());
		sp4.setProjet(projet);
		sp3.setSousProjetSuivant(sp4);
		
		listSousProjet.add(sp1);
		listSousProjet.add(sp2);
		listSousProjet.add(sp3);
		listSousProjet.add(sp4);
		
		try {
			projetAppImpl.enregistrerStructureProjet(projet.getId(), listSousProjet);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return listSousProjet;
	}
	
	private TacheVO getTache(SousProjetVO sp, String ordre, ExecuteurVO exec) {
		TacheVO tache = new TacheVO();
		tache.setNom("Tache " + ordre);
		tache.setDescription("test unitaire tache " + ordre);
		tache.setDateDebut(getDate(2014, 3, 25));
		tache.setDateFin(getDate(2014, 4, 1));
		List<ExecuteurVO> executeurs = new ArrayList<ExecuteurVO>();
		executeurs.add(exec);
		
		tache.setExecuteurs(executeurs );
		tache.setSousProjet(sp);
		
		return tache;
	}
	
	private SousProjetVO getSousProjet(String nomSp, ChefSousProjetVO chefProj, ProjetVO proj) {
		SousProjetVO sp = new SousProjetVO();
		SousProjetVO filter = new SousProjetVO();
		
		filter.setProjet(proj);
		filter.setChefSousProjet(chefProj);
		
		
		filter.setNom("sp 1");
		try {
			List<SousProjetVO> list = sousProjetService.getSousProjets(filter);
			sp = list.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sp;
	}
	
	
}
package com.tnt.gestionproj.test;

import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jbpm.task.service.mina.MinaTaskServer;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tnt.gestionproj.app.impl.ProjetAppImpl;
import com.tnt.gestionproj.app.impl.UserAppImpl;
import com.tnt.gestionproj.jbpm.KnowledgeTaskServiceSession;
import com.tnt.gestionproj.service.UserService;
import com.tnt.gestionproj.vo.AdministrateurVO;
import com.tnt.gestionproj.vo.ChefProjetVO;
import com.tnt.gestionproj.vo.LoginVO;
import com.tnt.gestionproj.vo.ProjetVO;
import com.tnt.gestionproj.vo.UserVO;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProjectAppTest {

	private static ClassPathXmlApplicationContext context;

	private static ProjetAppImpl projetAppImpl;
	private static UserService userService;
	
	@BeforeClass
	public static void setUp() throws Exception {
		System.setProperty("java.naming.factory.initial",
				"bitronix.tm.jndi.BitronixInitialContextFactory");
		
		context = new ClassPathXmlApplicationContext("applicationContext_test.xml");
		
		projetAppImpl = (ProjetAppImpl) context.getBean("projetApp");
		userService = (UserService) context.getBean("userService");
		
		KnowledgeTaskServiceSession taskServiceSession = (KnowledgeTaskServiceSession) context.getBean("knowledgeTaskServiceSession");
		MinaTaskServer server = new MinaTaskServer(taskServiceSession.getTaskService());
		Thread thread = new Thread(server);
		thread.start();
	}
	
	@Test
	public void structureProjet() {
		ProjetVO projet = new ProjetVO();
		projet.setNom("projet 132");
		projet.setAdministrateur(getAdministrateur());
		projet.setChefProjet(getChefProjet());
		projet.setDateDebut(getDate(2013, 6, 20));
		projet.setDateFin(getDate(2013, 11, 31));
		
		

		try {
			
			projetAppImpl.addProjet(projet);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	private AdministrateurVO getAdministrateur() {
		return (AdministrateurVO) searchUserByLogin("aba", "aba");
	}
	
	private ChefProjetVO getChefProjet() {
		return (ChefProjetVO) searchUserByLogin("aba_ch", "aba_ch");
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
}

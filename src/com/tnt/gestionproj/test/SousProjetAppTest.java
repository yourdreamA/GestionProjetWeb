package com.tnt.gestionproj.test;

import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jbpm.task.service.mina.MinaTaskServer;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tnt.gestionproj.app.impl.SousProjetAppImpl;
import com.tnt.gestionproj.jbpm.KnowledgeTaskServiceSession;
import com.tnt.gestionproj.service.SousProjetService;
import com.tnt.gestionproj.service.UserService;
import com.tnt.gestionproj.vo.AdministrateurVO;
import com.tnt.gestionproj.vo.ChefProjetVO;
import com.tnt.gestionproj.vo.ExecuteurVO;
import com.tnt.gestionproj.vo.LoginVO;
import com.tnt.gestionproj.vo.SousProjetVO;
import com.tnt.gestionproj.vo.TacheVO;
import com.tnt.gestionproj.vo.UserVO;


@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SousProjetAppTest {

	private static ClassPathXmlApplicationContext context;

	private static SousProjetAppImpl sousProjetAppImpl;
	private static SousProjetService sousProjetService;
	private static UserService userService;
	
	@BeforeClass
	public static void initHibernate() throws Exception {
		System.setProperty("java.naming.factory.initial",
				"bitronix.tm.jndi.BitronixInitialContextFactory");
		
		context = new ClassPathXmlApplicationContext("applicationContext_test.xml");
		
		sousProjetAppImpl = (SousProjetAppImpl) context.getBean("sousProjetApp");
		userService = (UserService) context.getBean("userService");
		sousProjetService = (SousProjetService) context.getBean("sousProjetService");
		
		KnowledgeTaskServiceSession taskServiceSession = (KnowledgeTaskServiceSession) context.getBean("knowledgeTaskServiceSession");
		MinaTaskServer server = new MinaTaskServer(taskServiceSession.getTaskService());
		Thread thread = new Thread(server);
		thread.start();
	}
	
	@Test
	public void getStructureProjet() {
		SousProjetVO sousProjet = getSousProjet();
		TacheVO tache = getTache(sousProjet);
		try {
			sousProjetAppImpl.addTask(sousProjet, tache);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
	}
	
	private TacheVO getTache(SousProjetVO sp) {
		TacheVO tache = new TacheVO();
		tache.setNom("Tache 1");
		tache.setDescription("test unitaire tache 1");
		tache.setDateDebut(getDate(2014, 3, 25));
		tache.setDateFin(getDate(2014, 4, 1));
		List<ExecuteurVO> executeurs = new ArrayList<ExecuteurVO>();
		executeurs.add(getExecuteur());
		tache.setExecuteurs(executeurs );
		tache.setSousProjet(sp);
		
		return tache;
	}
	
	private SousProjetVO getSousProjet() {
		SousProjetVO sp = new SousProjetVO();
		SousProjetVO filter = new SousProjetVO();
		filter.setNom("sp 1");
		try {
			List<SousProjetVO> list = sousProjetService.getSousProjets(filter);
			sp = list.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sp;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	private ExecuteurVO getExecuteur() {
		//return (ExecuteurVO) searchUserByLogin("aba_ex", "4262");
		return (ExecuteurVO) searchUserByLogin("aba_ex2", "7718");
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

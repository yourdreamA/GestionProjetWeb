package com.tnt.gestionproj.test;

import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tnt.gestionproj.service.ProjetService;
import com.tnt.gestionproj.service.UserService;
import com.tnt.gestionproj.vo.AdministrateurVO;
import com.tnt.gestionproj.vo.ChefProjetVO;
import com.tnt.gestionproj.vo.LoginVO;
import com.tnt.gestionproj.vo.ProjetVO;
import com.tnt.gestionproj.vo.UserVO;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProjetTest {

	private static ProjetService service;
	private static UserService userService;
	private static ClassPathXmlApplicationContext context;

	@BeforeClass
	public static void initHibernate() throws Exception {
		context = new ClassPathXmlApplicationContext("applicationContext.xml");
		service = (ProjetService) context.getBean("projetService");
		userService = (UserService) context.getBean("userService");
	}
	
	@Test
	public void addProjet() {
		ProjetVO projet = new ProjetVO();
		projet.setNom("projet 1");
		projet.setAdministrateur(getAdministrateur());
		projet.setDateDebut(getDate(2013, 6, 20));
		projet.setDateFin(getDate(2013, 11, 31));
		
		

		try {
			
			service.addProjet(projet);
		} catch (Exception e) {
			assertTrue(false);
		}
		
	}
	
	@Test
	public void affecterProjet() {
		
		ProjetVO filter = new ProjetVO();
		filter.setNom("projet 1");
		ProjetVO projet = service.getProjets(filter).get(0);
		
		projet.setChefProjet(getChefProjet());
		

		try {
			
			service.updateProjet(projet);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
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
}

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tnt.gestionproj.app.impl.UserAppImpl;
import com.tnt.gestionproj.service.UserService;
import com.tnt.gestionproj.vo.AdministrateurVO;
import com.tnt.gestionproj.vo.ChefProjetVO;
import com.tnt.gestionproj.vo.ChefSousProjetVO;
import com.tnt.gestionproj.vo.ExecuteurVO;
import com.tnt.gestionproj.vo.LoginVO;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAppTest {

	private static UserService service;
	private static UserAppImpl app;
	
	@Autowired
    private static ApplicationContext context;
	
	private void launch() {
		
	}
	
	@BeforeClass
	public static void initBean() throws Exception {
		System.setProperty("java.naming.factory.initial",
				"bitronix.tm.jndi.BitronixInitialContextFactory");
		context = new ClassPathXmlApplicationContext("applicationContext_test.xml");
		
		service = (UserService) context.getBean("userService");
		app = (UserAppImpl) context.getBean("userApp");
	}
	
	@Test
	public void addUserApp() {
		// add user
		AdministrateurVO admin = getAdministrateur();
		ChefSousProjetVO chsprj = getChefSousProjet();

		try {
			app.addUser(admin);
			app.addUser(getExecuteur());
			app.addUser(chsprj);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
	}
	
	private AdministrateurVO getAdministrateur() {
		LoginVO login = new LoginVO();
		login.setUsername("aba");
		login.setPassword("aba");

		AdministrateurVO user = new AdministrateurVO();
		user.setNom("abbassi");
		user.setPrenom("abderrahmen");
		user.setAdresse("11 rue italie");
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar dateNaissance = Calendar.getInstance();
		dateNaissance.set(Calendar.YEAR, 1985);
		dateNaissance.set(Calendar.MONTH, 0);
		dateNaissance.set(Calendar.DATE, 31);
		String dateNaissanceS = df.format(dateNaissance.getTime());
		
		try {
			dateNaissance.setTime(df.parse(dateNaissanceS));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		user.setDateNaissance(dateNaissance);
		
		
		user.setEmail("aba@gmail.com");
		user.setTelephone("+216 52144165");
		user.setLogin(login);
		return user;
	}
	
	private ChefSousProjetVO getChefSousProjet() {
		LoginVO login = new LoginVO();
		login.setUsername("aba_sch2");
		//login.setPassword("aba_sch");

		ChefSousProjetVO user = new ChefSousProjetVO();
		user.setNom("abbassi_sch");
		user.setPrenom("abderrahmen_sch");
		user.setAdresse("11 rue italie");
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar dateNaissance = Calendar.getInstance();
		dateNaissance.set(Calendar.YEAR, 1985);
		dateNaissance.set(Calendar.MONTH, 0);
		dateNaissance.set(Calendar.DATE, 31);
		String dateNaissanceS = df.format(dateNaissance.getTime());
		
		try {
			dateNaissance.setTime(df.parse(dateNaissanceS));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		user.setDateNaissance(dateNaissance);
		
		
		user.setEmail("aba@tnt.com");
		user.setTelephone("+216 52144165");
		user.setLogin(login);
		return user;
	}
	
	private ExecuteurVO getExecuteur() {
		LoginVO login = new LoginVO();
		login.setUsername("aba_ex2");
		login.setPassword("aba_ex2");

		ExecuteurVO user = new ExecuteurVO();
		user.setNom("zguimi");
		user.setPrenom("sana");
		user.setAdresse("11 rue italie");
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar dateNaissance = Calendar.getInstance();
		dateNaissance.set(Calendar.YEAR, 1985);
		dateNaissance.set(Calendar.MONTH, 0);
		dateNaissance.set(Calendar.DATE, 31);
		String dateNaissanceS = df.format(dateNaissance.getTime());
		
		try {
			dateNaissance.setTime(df.parse(dateNaissanceS));
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		user.setDateNaissance(dateNaissance);
		
		
		user.setEmail("aba@gmail.com");
		user.setTelephone("+216 52144165");
		user.setLogin(login);
		return user;
	}
	
}

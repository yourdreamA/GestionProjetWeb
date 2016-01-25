package com.tnt.gestionproj.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tnt.gestionproj.enums.TypeUserEnum;
import com.tnt.gestionproj.service.UserService;
import com.tnt.gestionproj.vo.AdministrateurVO;
import com.tnt.gestionproj.vo.ChefProjetVO;
import com.tnt.gestionproj.vo.ChefSousProjetVO;
import com.tnt.gestionproj.vo.ExecuteurVO;
import com.tnt.gestionproj.vo.LoginVO;
import com.tnt.gestionproj.vo.UserVO;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserTest {

	private static UserService service;
	
	@Autowired
    private static ApplicationContext context;
	
	@BeforeClass
	public static void initHibernate() throws Exception {
		context = new ClassPathXmlApplicationContext("applicationContext_test.xml");
		
		service = (UserService) context.getBean("userService");
	}
	
	
	//@Test
	public void addAdmin() {
		// add user
		AdministrateurVO user = getAdministrateur();

		try {
			service.addUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
	}
	
	//@Test
	public void addChefProjet() {
		// add user
		ChefProjetVO user = getChefProjet();

		try {
			service.addUser(user);
		} catch (Exception e) {
			assertTrue(false);
		}
		
	}
	
	//@Test
	public void addChefSousProjet() {
		// add user
		try {
			service.addUser(getChefSousProjet("aba_sch"));
			service.addUser(getChefSousProjet("aba_sch_2"));
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
	}
	
	@Test
	public void addExecuteur() {
		// add user
		try {
			service.addUser(getExecuteur());
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
	
	private ChefProjetVO getChefProjet() {
		LoginVO login = new LoginVO();
		login.setUsername("aba_ch");
		login.setPassword("aba_ch");

		ChefProjetVO user = new ChefProjetVO();
		user.setNom("abbassi_ch");
		user.setPrenom("abderrahmen_ch");
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
	

	@Test
	public void getUsers() {
		List<UserVO> list = null;
		try {
			list = service.getUsers(TypeUserEnum.Administrateur, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		assertNotNull(list);
		assertTrue(list.size() > 0);
		for (UserVO userVO : list) {
			System.out.println(userVO);
		}
	}
	
	@Test
	public void searchUserByLogin() {
		LoginVO login = new LoginVO();
		login.setUsername("aba");
		login.setPassword("aba");
		
		UserVO user = service.searchUserByLogin(login);
		assertNotNull(user);
			
		System.out.println(user);
	}
	
	
	private ChefSousProjetVO getChefSousProjet(String userName) {
		LoginVO login = new LoginVO();
		login.setUsername(userName);
		login.setPassword("1000");

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
		login.setUsername("aba_ex");
		login.setPassword("1000");

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

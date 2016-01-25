package com.tnt.gestionproj.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.gestionproj.dao.UserDao;
import com.tnt.gestionproj.entities.Administrateur;
import com.tnt.gestionproj.entities.ChefProjet;
import com.tnt.gestionproj.entities.ChefSousProjet;
import com.tnt.gestionproj.entities.Executeur;
import com.tnt.gestionproj.entities.Login;
import com.tnt.gestionproj.entities.User;
import com.tnt.gestionproj.enums.TypeUserEnum;
import com.tnt.gestionproj.service.UserService;
import com.tnt.gestionproj.vo.AdministrateurVO;
import com.tnt.gestionproj.vo.ChefProjetVO;
import com.tnt.gestionproj.vo.ChefSousProjetVO;
import com.tnt.gestionproj.vo.ExecuteurVO;
import com.tnt.gestionproj.vo.LoginVO;
import com.tnt.gestionproj.vo.UserVO;

@Service("userService")
//@RemotingDestination(value = "gestionUser", channels={"my-amf"})
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
	@Autowired
	private UserDao dao;

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void addUser(UserVO user) {
		dao.addUser(user.convertToEntitie());
	}

	@Override
	public List<UserVO> getUsers(TypeUserEnum typeUser, UserVO filter) throws Exception {
		List<UserVO> result = null;
		List<User> list = dao.getUsers(typeUser, filter);

		if (list != null && !list.isEmpty()) {
			result = new ArrayList<UserVO>();
			for (User user : list) {
				UserVO userVO = null;
				switch (typeUser) {
				case Administrateur:
					userVO = new AdministrateurVO(user);
					break;
				case ChefProjet:
					userVO = new ChefProjetVO(user);
					break;
				case ChefSousProjet:
					userVO = new ChefSousProjetVO(user);
					break;
				case Executeur:
					userVO = new ExecuteurVO(user);
					break;

				}
				 result.add(userVO);
			}
		}

		return result;
	}

	public void setDao(UserDao dao) {
		this.dao = dao;
	}

	@Override
	public UserVO searchUserById(long id) {
		UserVO userVO = null;
		
		User user = dao.searchUserById(id);
		
		if (user instanceof Administrateur) {
			userVO = new AdministrateurVO(user);
		} else if (user instanceof ChefProjet) {
			userVO = new ChefProjetVO(user);
		} else if (user instanceof ChefSousProjet) {
			userVO = new ChefSousProjetVO(user);
		} else if (user instanceof Executeur) {
			userVO = new ExecuteurVO(user);
		}
		
		return userVO;
	}

	@Override
	public void updateUser(UserVO user) {
		dao.updateUser(user.convertToEntitie());
		
	}

	@Override
	public UserVO searchUserByLogin(LoginVO login) {
		UserVO userVO = null;
		
		User user = dao.searchUserByLogin(login.convertToEntitie());
		
		if (user instanceof Administrateur) {
			userVO = new AdministrateurVO(user);
		} else if (user instanceof ChefProjet) {
			userVO = new ChefProjetVO(user);
		} else if (user instanceof ChefSousProjet) {
			userVO = new ChefSousProjetVO(user);
		} else if (user instanceof Executeur) {
			userVO = new ExecuteurVO(user);
		}
		
		return userVO;
	}

	@Override
	public UserVO login(LoginVO login) throws Exception {
		
		UserVO userVO = searchUserByLogin(login);
		
		if (userVO == null) {
			throw new Exception("Vérifier votre nom d'utilisateur et votre mot de passe.");
		}
		
		return userVO;
	}
	
	public UserVO generatePwd(UserVO user) {
		StringBuilder pwd = new StringBuilder();
		for (int i = 0; i < 4; i++) {
			pwd.append((int) (Math.random() * (9)));
		}
		
		user.getLogin().setPassword(pwd.toString());
		return user;
	}
	
	@Override
	public List<UserVO> getAllUser() {
		List<UserVO> result = null;
		List<User> list = dao.getAllUser();
		
		if (list != null && !list.isEmpty()) {
			result = new ArrayList<UserVO>();
			for (User user : list) {
				UserVO userVO = null;
				if (user instanceof Administrateur) {
					userVO = new AdministrateurVO(user);
				} else if (user instanceof ChefProjet) {
					userVO = new ChefProjetVO(user);
				} else if (user instanceof ChefSousProjet) {
					userVO = new ChefSousProjetVO(user);
				} else if (user instanceof Executeur) {
					userVO = new ExecuteurVO(user);
				}
				result.add(userVO);
			}
		}
		
		return result;
	}

	@Override
	public List<LoginVO> getAllLogin() {
		List<LoginVO> result = null;
		List<Login> list = dao.getAllLogin();
		
		if (list != null && !list.isEmpty()) {
			result = new ArrayList<LoginVO>();
			for (Login l : list) {
				result.add(new LoginVO(l));
			}
		}
		
		return result;
	}
	
	@Override
	public boolean existsUser(String userName) {
		return dao.searchLogin(userName) != null;
	}
}

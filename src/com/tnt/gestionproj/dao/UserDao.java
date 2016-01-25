package com.tnt.gestionproj.dao;

import java.util.List;

import com.tnt.gestionproj.entities.Login;
import com.tnt.gestionproj.entities.User;
import com.tnt.gestionproj.enums.TypeUserEnum;
import com.tnt.gestionproj.vo.LoginVO;
import com.tnt.gestionproj.vo.UserVO;

public interface UserDao {

	List<User> getUsers(TypeUserEnum typeUser, UserVO filter);
	User searchUserById(long id);
	void addUser(User user);
	void updateUser(User user);
	User searchUserByLogin(Login login);
	List<Login> getAllLogin();
	Login searchLogin(String userName);
	List<User> getAllUser();
}

package com.tnt.gestionproj.service;

import java.util.List;

import com.tnt.gestionproj.enums.TypeUserEnum;
import com.tnt.gestionproj.vo.LoginVO;
import com.tnt.gestionproj.vo.UserVO;

public interface UserService {

	List<UserVO> getUsers(TypeUserEnum typeUser, UserVO filter) throws Exception;

	void addUser(UserVO user);

	UserVO searchUserById(long id);

	void updateUser(UserVO user);
	UserVO searchUserByLogin(LoginVO login);
	UserVO login(LoginVO login) throws Exception;
	UserVO generatePwd(UserVO user);
	List<LoginVO> getAllLogin();

	boolean existsUser(String userName);

	List<UserVO> getAllUser();
}

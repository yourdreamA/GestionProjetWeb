package com.tnt.gestionproj.vo;

import java.io.Serializable;

import com.tnt.gestionproj.entities.Login;
import com.tnt.gestionproj.entities.User;


public class LoginVO implements Serializable {

	   
	private String username;
	private String password;
	private static final long serialVersionUID = 1L;

	public Login convertToEntitie() {
		Login login = new Login();
		login.setUsername(username);
		login.setPassword(password);
		
		return login;
	}
	
	public LoginVO() {
		// TODO Auto-generated constructor stub
	}
	public LoginVO(Login login) {
		this.username = login.getUsername();
		this.password = login.getPassword();
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}   
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

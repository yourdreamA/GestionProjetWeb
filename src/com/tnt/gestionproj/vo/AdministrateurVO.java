package com.tnt.gestionproj.vo;

import java.io.Serializable;

import com.tnt.gestionproj.entities.User;

public class AdministrateurVO extends UserVO implements Serializable {

	public AdministrateurVO() {
		// TODO Auto-generated constructor stub
	}

	public AdministrateurVO(User user) {
		super(user);
		// TODO Auto-generated constructor stub
	}

	private static final long serialVersionUID = 1L;
}

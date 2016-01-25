package com.tnt.gestionproj.vo;

import java.io.Serializable;
import java.util.Calendar;

import com.tnt.gestionproj.entities.Administrateur;
import com.tnt.gestionproj.entities.ChefProjet;
import com.tnt.gestionproj.entities.ChefSousProjet;
import com.tnt.gestionproj.entities.Executeur;
import com.tnt.gestionproj.entities.User;

public abstract class UserVO implements Serializable {

	   
	private long id;
	private String nom;
	private String prenom;
	private String adresse;
	private String telephone;
	private String email;
	private Calendar dateNaissance;
	
	private LoginVO login;
	
	
	
	
	
	public UserVO(User user) {
		super();
		this.id = user.getId();
		this.nom = user.getNom();
		this.prenom = user.getPrenom();
		this.adresse = user.getAdresse();
		this.telephone = user.getTelephone();
		this.email = user.getEmail();
		this.dateNaissance = user.getDateNaissance();
		this.login = new LoginVO(user.getLogin());
	}
	
	public UserVO() {
		// TODO Auto-generated constructor stub
	}

	public User convertToEntitie() {
		User user = null;
		if (this instanceof AdministrateurVO) {
			user = new Administrateur();
		} else if (this instanceof ChefProjetVO) {
			user = new ChefProjet();
		} else if (this instanceof ChefSousProjetVO) {
			user = new ChefSousProjet();
		} else if (this instanceof ExecuteurVO) {
			user = new Executeur();
		}
		
		user.setId(getId());
		user.setNom(getNom());
		user.setPrenom(getPrenom());
		user.setAdresse(getAdresse());
		user.setTelephone(getTelephone());
		user.setEmail(getEmail());
		user.setDateNaissance(getDateNaissance());
		user.setLogin(login.convertToEntitie());
		
		return user;
	}
	
	public LoginVO getLogin() {
		return login;
	}
	public void setLogin(LoginVO login) {
		this.login = login;
	}

	private static final long serialVersionUID = 1L;
   
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}   
	public String getNom() {
		return this.nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}   
	public String getPrenom() {
		return this.prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}   
	public String getAdresse() {
		return this.adresse;
	}

	public void setAdresse(String adresse) {
		this.adresse = adresse;
	}   
	public String getTelephone() {
		return this.telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}   
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}   
	public Calendar getDateNaissance() {
		return this.dateNaissance;
	}

	public void setDateNaissance(Calendar dateNaissance) {
		this.dateNaissance = dateNaissance;
	}
   
}

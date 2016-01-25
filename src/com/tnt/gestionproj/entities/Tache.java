package com.tnt.gestionproj.entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Tache
 *
 */
@Entity
public class Tache implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private String nom;
	private String description;
	private Calendar dateDebut;
	private Calendar dateFin;
	@ManyToMany
	private List<Executeur> executeurs;
	
	@OneToMany(mappedBy="tache")
	private List<DetailsTache> listDetails;


	//	@ManyToOne
//	private Projet projet;
	@ManyToOne
	private SousProjet sousProjet;
	
//	public Projet getProjet() {
//		return projet;
//	}
//
//	public void setProjet(Projet projet) {
//		this.projet = projet;
//	}

	public SousProjet getSousProjet() {
		return sousProjet;
	}

	public void setSousProjet(SousProjet sousProjet) {
		this.sousProjet = sousProjet;
	}

	public List<Executeur> getExecuteurs() {
		return executeurs;
	}

	public void setExecuteurs(List<Executeur> executeurs) {
		this.executeurs = executeurs;
	}

	private static final long serialVersionUID = 1L;

	public Tache() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Calendar getDateDebut() {
		return dateDebut;
	}

	public void setDateDebut(Calendar dateDebut) {
		this.dateDebut = dateDebut;
	}

	public Calendar getDateFin() {
		return dateFin;
	}

	public void setDateFin(Calendar dateFin) {
		this.dateFin = dateFin;
	}
	
	public List<DetailsTache> getListDetails() {
		return listDetails;
	}

	public void setListDetails(List<DetailsTache> listDetails) {
		this.listDetails = listDetails;
	}
}

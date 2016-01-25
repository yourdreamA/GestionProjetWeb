package com.tnt.gestionproj.entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Projet
 *
 */
@Entity

public class Projet implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private String nom;
	private Calendar dateDebut;
	private Calendar dateFin;
	@ManyToOne
	private Administrateur administrateur;
	@ManyToOne
	private ChefProjet chefProjet;
//	@OneToMany(mappedBy="projet")
//	private List<Tache> taches;
	@OneToMany(mappedBy="projet")
	private List<SousProjet> sousProjets;
	
	private String methodeGestion;
	
	private long idInstance;
	
	
	public long getIdInstance() {
		return idInstance;
	}

	public void setIdInstance(long idInstance) {
		this.idInstance = idInstance;
	}

//	public List<Tache> getTaches() {
//		return taches;
//	}
//
//	public void setTaches(List<Tache> taches) {
//		this.taches = taches;
//	}

	public List<SousProjet> getSousProjets() {
		return sousProjets;
	}

	public void setSousProjets(List<SousProjet> sousProjets) {
		this.sousProjets = sousProjets;
	}

	
	
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
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

	public ChefProjet getChefProjet() {
		return chefProjet;
	}

	public void setChefProjet(ChefProjet chefProjet) {
		this.chefProjet = chefProjet;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private static final long serialVersionUID = 1L;

	public Projet() {
		super();
	}

	public Administrateur getAdministrateur() {
		return administrateur;
	}

	public void setAdministrateur(Administrateur administrateur) {
		this.administrateur = administrateur;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Projet other = (Projet) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		return true;
	}

	public String getMethodeGestion() {
		return methodeGestion;
	}

	public void setMethodeGestion(String methodeGestion) {
		this.methodeGestion = methodeGestion;
	}
   
}

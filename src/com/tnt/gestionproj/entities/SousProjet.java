package com.tnt.gestionproj.entities;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.tnt.gestionproj.enums.TypeLiaisonEnum;

/**
 * Entity implementation class for Entity: SousProjet
 *
 */
@Entity

public class SousProjet implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;
	private String nom;
	private Calendar dateDebut;
	private Calendar dateFin;
	@ManyToOne
	private ChefSousProjet chefSousProjet;
	@OneToMany
	private List<Tache> taches;
	@ManyToOne
	private Projet projet;
	
	private String typeLiaison;
	
	@OneToOne
	private SousProjet sousProjetSuivant;
	
	public String getTypeLiaison() {
		return typeLiaison;
	}

	public void setTypeLiaison(String typeLiaison) {
		this.typeLiaison = typeLiaison;
	}

	
	
	
	public SousProjet getSousProjetSuivant() {
		return sousProjetSuivant;
	}

	public void setSousProjetSuivant(SousProjet sousProjetSuivant) {
		this.sousProjetSuivant = sousProjetSuivant;
	}

	public Projet getProjet() {
		return projet;
	}

	public void setProjet(Projet projet) {
		this.projet = projet;
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

	public ChefSousProjet getChefSousProjet() {
		return chefSousProjet;
	}

	public void setChefSousProjet(ChefSousProjet chefSousProjet) {
		this.chefSousProjet = chefSousProjet;
	}

	public List<Tache> getTaches() {
		return taches;
	}

	public void setTaches(List<Tache> taches) {
		this.taches = taches;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private static final long serialVersionUID = 1L;

	public SousProjet() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nom == null) ? 0 : nom.hashCode());
		result = prime * result + ((projet == null) ? 0 : projet.hashCode());
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
		SousProjet other = (SousProjet) obj;
		if (nom == null) {
			if (other.nom != null)
				return false;
		} else if (!nom.equals(other.nom))
			return false;
		if (projet == null) {
			if (other.projet != null)
				return false;
		} else if (!projet.equals(other.projet))
			return false;
		return true;
	}
   
}

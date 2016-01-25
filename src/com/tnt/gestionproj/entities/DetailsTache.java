package com.tnt.gestionproj.entities;

import com.tnt.gestionproj.entities.Executeur;
import java.io.Serializable;
import java.lang.String;
import java.util.Calendar;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: DetailsTache
 *
 */
@Entity

public class DetailsTache implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private long id;

	private String description;
	private String resume;
	private Calendar date;
	private int nombreHeure;
	private int avancement;
	@ManyToOne
	private Executeur executeur;
	@ManyToOne
	private Tache tache;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Tache getTache() {
		return tache;
	}
	public void setTache(Tache tache) {
		this.tache = tache;
	}

	private static final long serialVersionUID = 1L;

	public DetailsTache() {
		super();
	}   
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}   
	public Calendar getDate() {
		return this.date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}   
	public int getNombreHeure() {
		return this.nombreHeure;
	}

	public void setNombreHeure(int nombreHeure) {
		this.nombreHeure = nombreHeure;
	}   
	public int getAvancement() {
		return this.avancement;
	}

	public void setAvancement(int avancement) {
		this.avancement = avancement;
	}   
	public Executeur getExecuteur() {
		return this.executeur;
	}

	public void setExecuteur(Executeur executeur) {
		this.executeur = executeur;
	}
	public String getResume() {
		return resume;
	}
	public void setResume(String resume) {
		this.resume = resume;
	}
   
}

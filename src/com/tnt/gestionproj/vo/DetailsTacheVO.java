package com.tnt.gestionproj.vo;

import java.io.Serializable;
import java.util.Calendar;

import com.tnt.gestionproj.entities.DetailsTache;
import com.tnt.gestionproj.entities.Executeur;

/**
 * Entity implementation class for Entity: DetailsTache
 *
 */

public class DetailsTacheVO implements Serializable {

	private long id;

	private String description;
	private Calendar date;
	private int nombreHeure;
	private int avancement;
	private ExecuteurVO executeur;
	private TacheVO tache;
	private String resume;
	
	public DetailsTacheVO() {
		// TODO Auto-generated constructor stub
	}
	
	public DetailsTacheVO(DetailsTache det) {
		super();
		this.id = det.getId();
		this.description = det.getDescription();
		resume = det.getResume();
		this.date = det.getDate();
		this.nombreHeure = det.getNombreHeure();
		this.avancement = det.getAvancement();
		if (det.getExecuteur() != null) {
			this.executeur = new ExecuteurVO(det.getExecuteur());
		}
		
		//this.tache = tache;
	}
	
	public DetailsTache convertToEntitie() {
		DetailsTache entity = new DetailsTache();
		
		entity.setId(getId());
		entity.setResume(getResume());
		entity.setDescription(description);
		entity.setDate(date);
		entity.setNombreHeure(nombreHeure);
		entity.setAvancement(avancement);

		if (tache != null) {
			entity.setTache(tache.convertToEntitie());
		}
		
		if (executeur != null) {
			entity.setExecuteur((Executeur) executeur.convertToEntitie());
		}
		
		return entity;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public TacheVO getTache() {
		return tache;
	}
	public void setTache(TacheVO tache) {
		this.tache = tache;
	}

	private static final long serialVersionUID = 1L;

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
	public ExecuteurVO getExecuteur() {
		return this.executeur;
	}

	public void setExecuteur(ExecuteurVO executeur) {
		this.executeur = executeur;
	}
	public String getResume() {
		return resume;
	}
	public void setResume(String resume) {
		this.resume = resume;
	}
   
}

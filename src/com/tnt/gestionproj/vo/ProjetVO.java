package com.tnt.gestionproj.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.tnt.gestionproj.entities.Administrateur;
import com.tnt.gestionproj.entities.ChefProjet;
import com.tnt.gestionproj.entities.Projet;
import com.tnt.gestionproj.entities.SousProjet;

/**
 * Entity implementation class for Entity: Projet
 *
 */

public class ProjetVO implements Serializable {

	private long id;
	private String nom;
	private Calendar dateDebut;
	private Calendar dateFin;
	private AdministrateurVO administrateur;
	private ChefProjetVO chefProjet;
	private List<TacheVO> taches;
	private String status;
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private List<SousProjetVO> sousProjets;
	
	private String methodeGestion;
	
	private long idInstance;
	
	
	public long getIdInstance() {
		return idInstance;
	}

	public void setIdInstance(long idInstance) {
		this.idInstance = idInstance;
	}

	public ProjetVO() {}
	
	public ProjetVO(Projet projet) {
		super();
		this.id = projet.getId();
		this.nom = projet.getNom();
		methodeGestion = projet.getMethodeGestion();
		this.dateDebut = projet.getDateDebut();
		this.dateFin = projet.getDateFin();
		if (projet.getAdministrateur() != null) {
			this.administrateur = new AdministrateurVO(projet.getAdministrateur());
		}
		
		if (projet.getChefProjet() != null) {
			this.chefProjet = new ChefProjetVO(projet.getChefProjet());
		}
		//TODO
//		this.taches = taches;
		
//		this.sousProjets = sousProjets;
		idInstance = projet.getIdInstance();
	}
	
	public ProjetVO(Projet projet, boolean withSp) {
		super();
		this.id = projet.getId();
		this.nom = projet.getNom();
		methodeGestion = projet.getMethodeGestion();
		this.dateDebut = projet.getDateDebut();
		this.dateFin = projet.getDateFin();
		if (projet.getAdministrateur() != null) {
			this.administrateur = new AdministrateurVO(projet.getAdministrateur());
		}
		
		if (projet.getChefProjet() != null) {
			this.chefProjet = new ChefProjetVO(projet.getChefProjet());
		}
		
		if (projet.getSousProjets() != null && !projet.getSousProjets().isEmpty()) {
			this.sousProjets = new ArrayList<SousProjetVO>();
			for (SousProjet sp : projet.getSousProjets()) {
				sousProjets.add(new SousProjetVO(sp));
			}
		}
		
		idInstance = projet.getIdInstance();
	}

	public Projet convertToEntitie() {
		Projet entity = new Projet();
		
		entity.setId(getId());
		entity.setNom(getNom());
		entity.setMethodeGestion(methodeGestion);
		entity.setDateDebut(dateDebut);
		entity.setDateFin(dateFin);
		if (administrateur != null) {
			entity.setAdministrateur((Administrateur) administrateur.convertToEntitie());
		}
		if (chefProjet != null) {
			entity.setChefProjet((ChefProjet) chefProjet.convertToEntitie());
		}
		//TODO convert taches
		
		if (sousProjets != null && !sousProjets.isEmpty()) {
			List<SousProjet> sousProjetsEntity = new ArrayList<SousProjet>();
			for (SousProjetVO sp : sousProjets) {
				sousProjetsEntity.add(sp.convertToEntitie());
			}
			entity.setSousProjets(sousProjetsEntity);
		}
		
		entity.setIdInstance(idInstance);
		
		return entity;
	}
	
	public List<TacheVO> getTaches() {
		return taches;
	}

	public void setTaches(List<TacheVO> taches) {
		this.taches = taches;
	}

	public List<SousProjetVO> getSousProjets() {
		return sousProjets;
	}

	public void setSousProjets(List<SousProjetVO> sousProjets) {
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

	public ChefProjetVO getChefProjet() {
		return chefProjet;
	}

	public void setChefProjet(ChefProjetVO chefProjet) {
		this.chefProjet = chefProjet;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private static final long serialVersionUID = 1L;


	public AdministrateurVO getAdministrateur() {
		return administrateur;
	}

	public void setAdministrateur(AdministrateurVO administrateur) {
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
		ProjetVO other = (ProjetVO) obj;
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

	@Override
	public String toString() {
		return "ProjetVO [id=" + id + ", nom=" + nom + ", dateDebut="
				+ dateDebut + ", dateFin=" + dateFin + ", administrateur="
				+ administrateur + ", chefProjet=" + chefProjet + ", taches="
				+ taches + ", status=" + status + ", sousProjets="
				+ sousProjets + "]";
	}
   
}

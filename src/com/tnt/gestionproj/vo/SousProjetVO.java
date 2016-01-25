package com.tnt.gestionproj.vo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

import com.tnt.gestionproj.entities.ChefSousProjet;
import com.tnt.gestionproj.entities.SousProjet;
import com.tnt.gestionproj.entities.Tache;
import com.tnt.gestionproj.enums.TypeLiaisonEnum;

import flex.messaging.io.ArrayList;

/**
 * Entity implementation class for Entity: SousProjet
 *
 */

public class SousProjetVO implements Serializable {

	private long id;
	private String nom;
	private Calendar dateDebut;
	private Calendar dateFin;
	private ChefSousProjetVO chefSousProjet;
	private List<TacheVO> taches;
	private ProjetVO projet;
	
	private TypeLiaisonEnum typeLiaison;
	private SousProjetVO sousProjetSuivant;
	
	private String status;
	private int progression;
	

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	
	public SousProjetVO() {
		// TODO Auto-generated constructor stub
	}
	
	public SousProjetVO(SousProjet sousProjet) {
		super();
		this.id = sousProjet.getId();
		this.nom = sousProjet.getNom();
		this.dateDebut = sousProjet.getDateDebut();
		this.dateFin = sousProjet.getDateFin();
		
		//this.chefSousProjet = sousProjet.getChefSousProjet();
		//this.taches = sousProjet.getTaches();
		
		if (sousProjet.getChefSousProjet() != null) {
			this.chefSousProjet = new ChefSousProjetVO(sousProjet.getChefSousProjet());
		}
		
		if (sousProjet.getProjet() != null) {
			projet = new ProjetVO(sousProjet.getProjet());
		}
		
		if (sousProjet.getTypeLiaison() != null) {
			if (TypeLiaisonEnum.SEQUENCE_FLOW.name()
							.equals(sousProjet.getTypeLiaison())) {
				
			}
			this.typeLiaison = TypeLiaisonEnum.SEQUENCE_FLOW;
		}
		
		if (sousProjet.getSousProjetSuivant() != null) {
			this.sousProjetSuivant = new SousProjetVO(
					sousProjet.getSousProjetSuivant());
		}
		
		if (sousProjet.getTaches() != null && !sousProjet.getTaches().isEmpty()) {
			taches = new ArrayList();
			for (Tache t : sousProjet.getTaches()) {
				taches.add(new TacheVO(t));
			}
		}
		
	}
	
	public SousProjet convertToEntitie() {
		SousProjet sousProjet = new SousProjet();
		
		sousProjet.setId(id);
		sousProjet.setNom(nom);
		sousProjet.setDateDebut(dateDebut);
		sousProjet.setDateFin(dateFin);
		if (typeLiaison != null) {
			sousProjet.setTypeLiaison(typeLiaison.name());
		}
		
		if (sousProjetSuivant != null) {
			sousProjet.setSousProjetSuivant(sousProjetSuivant.convertToEntitie());
		}
		
		if (projet != null) {
			sousProjet.setProjet(projet.convertToEntitie());
		}
		
		if (chefSousProjet != null) {
			sousProjet.setChefSousProjet((ChefSousProjet) chefSousProjet
					.convertToEntitie());
		}
		
		//taches
		
		return sousProjet;
	}

	public TypeLiaisonEnum getTypeLiaison() {
		return typeLiaison;
	}

	public void setTypeLiaison(TypeLiaisonEnum typeLiaison) {
		this.typeLiaison = typeLiaison;
	}

	public SousProjetVO getSousProjetSuivant() {
		return sousProjetSuivant;
	}

	public void setSousProjetSuivant(SousProjetVO sousProjetSuivant) {
		this.sousProjetSuivant = sousProjetSuivant;
	}

	public ProjetVO getProjet() {
		return projet;
	}

	public void setProjet(ProjetVO projet) {
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

	public ChefSousProjetVO getChefSousProjet() {
		return chefSousProjet;
	}

	public void setChefSousProjet(ChefSousProjetVO chefSousProjet) {
		this.chefSousProjet = chefSousProjet;
	}

	public List<TacheVO> getTaches() {
		return taches;
	}

	public void setTaches(List<TacheVO> taches) {
		this.taches = taches;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	private static final long serialVersionUID = 1L;


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
		SousProjetVO other = (SousProjetVO) obj;
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

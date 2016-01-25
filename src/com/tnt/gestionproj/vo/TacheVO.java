package com.tnt.gestionproj.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.tnt.gestionproj.entities.DetailsTache;
import com.tnt.gestionproj.entities.Executeur;
import com.tnt.gestionproj.entities.Tache;

/**
 * Entity implementation class for Entity: Tache
 *
 */

public class TacheVO implements Serializable {

	@Override
	public String toString() {
		return "TacheVO [id=" + id + ", nom=" + nom + ", description="
				+ description + ", dateDebut=" + dateDebut + ", dateFin="
				+ dateFin + ", executeurs=" + executeurs + ", projet=" + projet
				+ ", sousProjet=" + sousProjet + ", status=" + status + "]";
	}

	private long id;
	private String nom;
	private String description;
	private Calendar dateDebut;
	private Calendar dateFin;
	private List<ExecuteurVO> executeurs;
	private ProjetVO projet;
	private SousProjetVO sousProjet;
	private List<DetailsTacheVO> listDetails;
	
	//presentation
	private String status;
	private int progression;
	private int nbrHeures;
	
	//filtre
	private ExecuteurVO executeurUnique;
	private String nomProjet;
	private String nomSousProjet;
	
	public String getNomProjet() {
		return nomProjet;
	}

	public String getNomSousProjet() {
		return nomSousProjet;
	}

	public void setNomProjet(String nomProjet) {
		this.nomProjet = nomProjet;
	}

	public void setNomSousProjet(String nomSousProjet) {
		this.nomSousProjet = nomSousProjet;
	}

	public ExecuteurVO getExecuteurUnique() {
		return executeurUnique;
	}

	public void setExecuteurUnique(ExecuteurVO executeurUnique) {
		this.executeurUnique = executeurUnique;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public TacheVO() {
		// TODO Auto-generated constructor stub
	}
	
	public TacheVO(Tache t) {
		super();
		this.id = t.getId();
		this.nom = t.getNom();
		this.description = t.getDescription();
		this.dateDebut = t.getDateDebut();
		this.dateFin = t.getDateFin();
		if (t.getExecuteurs() != null && !t.getExecuteurs().isEmpty()) {
			this.executeurs = new ArrayList<ExecuteurVO>();
			for (Executeur exec : t.getExecuteurs()) {
				executeurs.add(new ExecuteurVO(exec));
			}
		}
//		if (t.getProjet() != null) {
//			this.projet = new ProjetVO(t.getProjet());
//		}
		if (t.getSousProjet() != null) {
			this.sousProjet = new SousProjetVO(t.getSousProjet());
		}
		nbrHeures = 0;
		progression = 0;
		if (t.getListDetails() != null && !t.getListDetails().isEmpty()) {
			this.listDetails = new ArrayList<DetailsTacheVO>();
			for (DetailsTache det : t.getListDetails()) {
				listDetails.add(new DetailsTacheVO(det));
				nbrHeures += det.getNombreHeure();
				progression = Math.max(progression, det.getAvancement());
			}
		}
	}
	
	public Tache convertToEntitie() {
		Tache entity = new Tache();
		
		entity.setId(getId());
		entity.setNom(getNom());
		entity.setDescription(description);
		entity.setDateDebut(dateDebut);
		entity.setDateFin(dateFin);
//		if (projet != null) {
//			entity.setProjet(projet.convertToEntitie());
//		}
		if (sousProjet != null) {
			entity.setSousProjet(sousProjet.convertToEntitie());
		}
		
		if (executeurs != null && !executeurs.isEmpty()) {
			List<Executeur> listExecuteurs = new ArrayList<Executeur>();
			for (ExecuteurVO exec : executeurs) {
				if (exec != null) {
					listExecuteurs.add((Executeur) exec.convertToEntitie());
				}
				
			}
			entity.setExecuteurs(listExecuteurs);
		}
		
		return entity;
	}

	public ProjetVO getProjet() {
		return projet;
	}

	public void setProjet(ProjetVO projet) {
		this.projet = projet;
	}

	public SousProjetVO getSousProjet() {
		return sousProjet;
	}

	public void setSousProjet(SousProjetVO sousProjet) {
		this.sousProjet = sousProjet;
	}

	public List<ExecuteurVO> getExecuteurs() {
		return executeurs;
	}

	public void setExecuteurs(List<ExecuteurVO> executeurs) {
		this.executeurs = executeurs;
	}

	private static final long serialVersionUID = 1L;


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

	public List<DetailsTacheVO> getListDetails() {
		return listDetails;
	}

	public void setListDetails(List<DetailsTacheVO> listDetails) {
		this.listDetails = listDetails;
	}

	public int getProgression() {
		return progression;
	}

	public int getNbrHeures() {
		return nbrHeures;
	}

	public void setProgression(int progression) {
		this.progression = progression;
	}

	public void setNbrHeures(int nbrHeures) {
		this.nbrHeures = nbrHeures;
	}
	
	
   
}

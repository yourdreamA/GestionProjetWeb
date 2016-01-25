package com.tnt.gestionproj.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.gestionproj.dao.SousProjetDao;
import com.tnt.gestionproj.entities.SousProjet;
import com.tnt.gestionproj.service.SousProjetService;
import com.tnt.gestionproj.vo.SousProjetVO;

@Service("sousProjetService")
//@RemotingDestination(value = "sousProjetService", channels={"my-amf"})
@Transactional(readOnly = true)
public class SousProjetServiceImpl implements SousProjetService {

	@Autowired
	private SousProjetDao dao;
	
	@Override
	public String validateStructureProjet(List<SousProjetVO> listSousProjet) throws Exception {
		StringBuilder erreur = new StringBuilder();
		Set<String> listNom = new HashSet<String>();
		
		for (SousProjetVO ss : listSousProjet) {
			//validation nom sous projet
			if (ss.getNom() == null || ss.getNom().isEmpty()) {
				erreur.append("Nom sous projet ne peut pas être vide.");
				break;
			}
			//validation nom sous projet unique
			if (listNom.contains(ss.getNom())) {
				erreur.append("Nom sous projet doit être unique.");
				break;
			} else {
				listNom.add(ss.getNom());
			}
			
			//validation test date debut
			if (ss.getDateDebut() == null) {
				erreur.append("- ").append(ss.getNom()).append(" : ").append("- date debut ne peut pas être vide.\n");
			}
			//validation affectation
			if (ss.getChefSousProjet() == null) {
				erreur.append("- ").append(ss.getNom()).append(" : ").append("- chef sous projet ne peut pas être vide.\n");
			}
		}
		
		//TODO test sous projet non lié
		
		return erreur.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.tnt.gestionproj.service.SousProjetService#enregistrerStructureProjet(java.util.List)
	 */
	@Override
	public String enregistrerStructureProjet(List<SousProjetVO> listSousProjet) {
		
		//recherche du dernier sous projet
		SousProjetVO dernier = null;
		for (Iterator<SousProjetVO> iterator = listSousProjet.iterator(); iterator.hasNext();) {
			SousProjetVO sousProjetVO = (SousProjetVO) iterator.next();
			if (sousProjetVO.getSousProjetSuivant() == null) {
				dernier = sousProjetVO;
				iterator.remove();
				break;
			}
		}
		
		//recherche du sous projet parent
		SousProjetVO parent = null;
		int rangMax = 0;
		for (SousProjetVO sousProjetVO : listSousProjet) {
			int rang = isParent(sousProjetVO.getSousProjetSuivant(), dernier, 0);
			if (rang > rangMax) {
				rangMax = rang;
				parent = sousProjetVO;
			}
		}
		
		updateTree(parent, null);
		//dao.update(projet.convertToEntitie());

		return "OK";
	}
	
	/**
	 * 
	 * @param sousProjet
	 * @param parent
	 */
	private void updateTree(SousProjetVO sousProjet, SousProjetVO parent) {
		if (sousProjet != null && sousProjet.getSousProjetSuivant() != null) {
			updateTree(sousProjet.getSousProjetSuivant(), sousProjet);
		}
		
		SousProjet newSP = dao.update(sousProjet.convertToEntitie());
		
		//MAJ parent
		if (parent != null) {
			parent.getSousProjetSuivant().setId(newSP.getId());
		}
	}
	
	/**
	 * 
	 * @param suivant
	 * @param dernier
	 * @param rang
	 * @return
	 */
	private int isParent(SousProjetVO suivant, SousProjetVO dernier, int rang) {
		if (suivant == null) {
			return rang;
		}
		if (suivant.equals(dernier)) {
			return rang + 1;
		} else {
			return isParent(suivant.getSousProjetSuivant(), dernier, rang + 1);
		}
	}
	
	@Override
	public SousProjetVO getStructureProjet(long idProjet) {
		SousProjetVO parent = null;
		
		List<SousProjet> list = dao.getSousProjets(idProjet);
		List<SousProjetVO> listVO = new ArrayList<SousProjetVO>();
		
		if (list != null && !list.isEmpty()) {
			for (SousProjet p : list) {
				listVO.add(new SousProjetVO(p));
			}
		}
		
		
		//recherche du dernier sous projet
		SousProjetVO dernier = null;
		for (Iterator<SousProjetVO> iterator = listVO.iterator(); iterator.hasNext();) {
			SousProjetVO sousProjetVO = (SousProjetVO) iterator.next();
			if (sousProjetVO.getSousProjetSuivant() == null) {
				dernier = sousProjetVO;
				iterator.remove();
				break;
			}
		}
		
		//recherche du sous projet parent
		int rangMax = 0;
		for (SousProjetVO sousProjetVO : listVO) {
			int rang = isParent(sousProjetVO.getSousProjetSuivant(), dernier, 0);
			if (rang > rangMax) {
				rangMax = rang;
				parent = sousProjetVO;
			}
		}

		return parent;
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public List<SousProjetVO> getSousProjets(SousProjetVO filter) throws Exception {
		List<SousProjetVO> result = null;
		List<SousProjet> list = dao.getSousProjets(filter);

		if (list != null && !list.isEmpty()) {
			result = new ArrayList<SousProjetVO>();
			for (SousProjet p : list) {
				result.add(new SousProjetVO(p));
			}
		}

		return result;
	}
	

}

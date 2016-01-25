package com.tnt.gestionproj.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.tnt.gestionproj.dao.SousProjetDao;
import com.tnt.gestionproj.entities.SousProjet;
import com.tnt.gestionproj.vo.SousProjetVO;

@Repository
public class SousProjetDaoImpl implements SousProjetDao {

	@PersistenceContext(unitName="GestionProjetJPA")
	private EntityManager entityManager;

	@Override
	public SousProjet update(SousProjet sousProjet) {
		return entityManager.merge(sousProjet);

	}

	public List<SousProjet> getSousProjets(long idProjet) {
		StringBuilder queryString = new StringBuilder("from SousProjet")
				.append(" where 1=1 ");

		queryString.append(" and projet.id = :idProjet");
		

		Query query = entityManager.createQuery(queryString.toString());
		query.setParameter("idProjet", idProjet);
		
		List<SousProjet> resultList = query.getResultList();

		return resultList;
	}
	
	public List<SousProjet> getSousProjets(SousProjetVO filter) {
    	StringBuilder queryString = new StringBuilder("from SousProjet")
    	.append(" where 1=1 ");
    	
    	Map<String, Object> params = null;
    	if (filter != null) {
    		params = new HashMap<String, Object>();
    		if (filter.getNom() != null && !filter.getNom().isEmpty()) {
    			queryString.append(" and nom = :nom");
    			params.put("nom", filter.getNom());
    		}
    		if (filter.getChefSousProjet() != null && filter.getChefSousProjet().getId() > 0) {
    			queryString.append(" and chefSousProjet.id = :idChefSousProjet");
    			params.put("idChefSousProjet", filter.getChefSousProjet().getId());
    		}
    		
    		if (filter.getProjet() != null && filter.getProjet().getId() > 0) {
    			queryString.append(" and projet.id = :idProjet");
    			params.put("idProjet", filter.getProjet().getId());
    		}
    		
    		if (filter.getDateDebut() != null) {
    			queryString.append(" and dateDebut = :dateDebut");
    			params.put("dateDebut", filter.getDateDebut());
    		}
    		
    		if (filter.getDateFin() != null) {
    			queryString.append(" and dateFin = :dateFin");
    			params.put("dateFin", filter.getDateFin());
    		}
    		
    		if (filter.getProjet() != null && filter.getProjet().getNom() != null && !filter.getProjet().getNom().isEmpty()) {
    			queryString.append(" and projet.nom = :nomProjet");
    			params.put("nomProjet", filter.getProjet().getNom());
    		}
    	}
    	
        Query query = entityManager.createQuery(queryString.toString());
        
        if (filter != null && params != null && !params.isEmpty()) {
        	for (String key : params.keySet()) {
        		query.setParameter(key, params.get(key));
        	}
        }
        
        List<SousProjet> resultList = query.getResultList();
        
        return resultList;
    }

}

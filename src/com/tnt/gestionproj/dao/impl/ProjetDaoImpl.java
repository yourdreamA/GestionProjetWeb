package com.tnt.gestionproj.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.tnt.gestionproj.dao.ProjetDao;
import com.tnt.gestionproj.entities.Projet;
import com.tnt.gestionproj.vo.ProjetVO;

@Repository
public class ProjetDaoImpl implements ProjetDao {

	@PersistenceContext(unitName="GestionProjetJPA")
	private EntityManager entityManager;
	
	public List<Projet> getAll() {
    	StringBuilder queryString = new StringBuilder("from Projet");
    	
        Query query = entityManager.createQuery(queryString.toString());
        List<Projet> resultList = query.getResultList();
        
        return resultList;
    }
	
	public List<Projet> getProjets(ProjetVO filter) {
    	StringBuilder queryString = new StringBuilder("from Projet")
    	.append(" where 1=1 ");
    	
    	Map<String, Object> params = null;
    	if (filter != null) {
    		params = new HashMap<String, Object>();
    		if (filter.getNom() != null && !filter.getNom().isEmpty()) {
    			queryString.append(" and nom = :nom");
    			params.put("nom", filter.getNom());
    		}
    		if (filter.getChefProjet() != null && filter.getChefProjet().getId() > 0) {
    			queryString.append(" and chefProjet.id = :idChefProjet");
    			params.put("idChefProjet", filter.getChefProjet().getId());
    		}
    		
    		if (filter.getDateDebut() != null) {
    			queryString.append(" and dateDebut = :dateDebut");
    			params.put("dateDebut", filter.getDateDebut());
    		}
    		
    		if (filter.getDateFin() != null) {
    			queryString.append(" and dateFin = :dateFin");
    			params.put("dateFin", filter.getDateFin());
    		}
    	}
    	
        Query query = entityManager.createQuery(queryString.toString());
        
        if (filter != null && params != null && !params.isEmpty()) {
        	for (String key : params.keySet()) {
        		query.setParameter(key, params.get(key));
        	}
        }
        
        List<Projet> resultList = query.getResultList();
        
        return resultList;
    }
    
    

	public void add(Projet projet) {
		entityManager.persist(projet);
		
	}
	
	public Projet searchById(long id) {
		return entityManager.find(Projet.class, id);
	}
	
	public void update(Projet projet) {
		entityManager.merge(projet);
	}
}

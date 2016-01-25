package com.tnt.gestionproj.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.tnt.gestionproj.dao.TacheDao;
import com.tnt.gestionproj.entities.DetailsTache;
import com.tnt.gestionproj.entities.Tache;
import com.tnt.gestionproj.vo.TacheVO;

@Repository
public class TacheDaoImpl implements TacheDao {

	@PersistenceContext(unitName="GestionProjetJPA")
	private EntityManager entityManager;
	
	@Override
	public List<Tache> getAll() {
    	StringBuilder queryString = new StringBuilder("from Tache");
    	
        Query query = entityManager.createQuery(queryString.toString());
        List<Tache> resultList = query.getResultList();
        
        return resultList;
    }

	@Override
	public void add(Tache tache) {
		entityManager.persist(tache);

	}
	
	@Override
	public void addDetails(DetailsTache det) {
		entityManager.persist(det);

	}

	@Override
	public Tache searchById(long id) {
		return entityManager.find(Tache.class, id);
	}

	@Override
	public void update(Tache tache) {
		entityManager.merge(tache);
	}

	@Override
	public List<Tache> getTaches(TacheVO filter) {
    	StringBuilder queryString = new StringBuilder("from Tache as t")
    	.append(" where 1=1 ");
    	
    	Map<String, Object> params = null;
    	if (filter != null) {
    		params = new HashMap<String, Object>();
    		if (filter.getNom() != null && !filter.getNom().isEmpty()) {
    			queryString.append(" and t.nom like :nom");
    			params.put("nom", filter.getNom() + "%");
    		}
    		if (filter.getExecuteurUnique() != null && filter.getExecuteurUnique().getId() > 0) {
    			queryString.append(" and exists (select 1 from Executeur as e where e.id="
    					+ filter.getExecuteurUnique().getId() + " and size(t.executeurs) > 0 and e in elements(t.executeurs)) ");
    			
    		}
    		
    		if (filter.getDateDebut() != null) {
    			queryString.append(" and t.dateDebut = :dateDebut");
    			params.put("dateDebut", filter.getDateDebut());
    		}
    		
    		if (filter.getDateFin() != null) {
    			queryString.append(" and t.dateFin = :dateFin");
    			params.put("dateFin", filter.getDateFin());
    		}
    		
    		if (filter.getProjet() != null && filter.getProjet().getId() > 0) {
    			queryString.append(" and t.projet.id = :idprojet");
    			params.put("idprojet", filter.getProjet().getId());
    		}
    		
    		if (filter.getSousProjet() != null && filter.getSousProjet().getId() > 0) {
    			queryString.append(" and t.sousProjet.id = :idsousProjet");
    			params.put("idsousProjet", filter.getSousProjet().getId());
    		}
    		
    		if (filter.getNomSousProjet() != null && !filter.getNomSousProjet().isEmpty()) {
    			queryString.append(" and t.sousProjet.nom like :nomSousProjet");
    			params.put("nomSousProjet", filter.getNomSousProjet() + "%");
    		}
    		
    		if (filter.getNomProjet() != null && !filter.getNomProjet().isEmpty()) {
    			queryString.append(" and t.sousProjet.projet.nom like :nomProjet");
    			params.put("nomProjet", filter.getNomProjet() + "%");
    		}
    	}
    	
    	queryString.append(" order by t.dateDebut");
        Query query = entityManager.createQuery(queryString.toString());
        
        if (filter != null && params != null && !params.isEmpty()) {
        	for (String key : params.keySet()) {
        		query.setParameter(key, params.get(key));
        	}
        }
        
        List<Tache> resultList = query.getResultList();
        
        return resultList;
    }

}

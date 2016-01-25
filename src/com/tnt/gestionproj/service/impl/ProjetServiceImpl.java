package com.tnt.gestionproj.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.gestionproj.dao.ProjetDao;
import com.tnt.gestionproj.entities.Projet;
import com.tnt.gestionproj.service.ProjetService;
import com.tnt.gestionproj.service.TacheService;
import com.tnt.gestionproj.vo.ProjetVO;
import com.tnt.gestionproj.vo.SousProjetVO;
import com.tnt.gestionproj.vo.TacheVO;

@Service("projetService")
//@RemotingDestination(value = "projetService", channels={"my-amf"})
@Transactional(readOnly = true)
public class ProjetServiceImpl implements ProjetService {

	@Autowired
	private ProjetDao dao;
	
	@Autowired
	private TacheService tacheService;
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public ProjetVO addProjet(ProjetVO projet) {
		
		Projet proj = projet.convertToEntitie();
		dao.add(proj);
		projet.setId(proj.getId());
		
		return projet;
	}

	@Override
	public List<ProjetVO> getProjets(ProjetVO filter) {
		List<ProjetVO> result = null;
		List<Projet> list = dao.getProjets(filter);

		if (list != null && !list.isEmpty()) {
			result = new ArrayList<ProjetVO>();
			for (Projet p : list) {
				result.add(new ProjetVO(p));
			}
		}

		return result;
	}

	@Override
	public ProjetVO searchById(long id) {
		ProjetVO projet = null;
		
		Projet p = dao.searchById(id);
		
		if (p != null) {
			projet = new ProjetVO(p);
		}
		
		return projet;
	}
	
	@Override
	public ProjetVO searchProjetForGantt(long id) {
		ProjetVO projet = null;
		
		Projet p = dao.searchById(id);
		
		if (p != null) {
			projet = new ProjetVO(p, true);
			if (projet.getSousProjets() != null && !projet.getSousProjets().isEmpty()) {
				for (SousProjetVO sp : projet.getSousProjets()) {
					TacheVO filter = new TacheVO();
					filter.setSousProjet(sp);
					List<TacheVO> taches = tacheService.getTaches(filter );
					sp.setTaches(taches);
				}
			}
		}
		
		return projet;
	}

	@Override
	public void updateProjet(ProjetVO projet) {
		dao.update(projet.convertToEntitie());
		
	}
	
	@Override
	public boolean existsProjet(String nomProjet) {
		ProjetVO filter = new ProjetVO();
		filter.setNom(nomProjet);
		List<Projet> list = dao.getProjets(filter);
		
		return list != null && !list.isEmpty();
	}

}

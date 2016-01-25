package com.tnt.gestionproj.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.tnt.gestionproj.dao.TacheDao;
import com.tnt.gestionproj.entities.DetailsTache;
import com.tnt.gestionproj.entities.Projet;
import com.tnt.gestionproj.entities.Tache;
import com.tnt.gestionproj.service.TacheService;
import com.tnt.gestionproj.vo.DetailsTacheVO;
import com.tnt.gestionproj.vo.ProjetVO;
import com.tnt.gestionproj.vo.TacheVO;

@Service("tacheService")
@Transactional(readOnly = true)
public class TacheServiceImpl implements TacheService {

	@Autowired
	private TacheDao dao;
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String addTache(TacheVO tache) throws Exception{
		Tache bo = tache.convertToEntitie();
		dao.add(bo);
		tache.setId(bo.getId());
		return "Tache ajouté avec succès.";
	}
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public String addDetailsTache(DetailsTacheVO det) throws Exception {
		DetailsTache bo = det.convertToEntitie();
		dao.addDetails(bo);
		det.setId(bo.getId());
		
		return "OK";
	}

	@Override
	public List<TacheVO> getTaches(TacheVO filter) {
		List<TacheVO> result = null;
		List<Tache> list = dao.getTaches(filter);

		if (list != null && !list.isEmpty()) {
			result = new ArrayList<TacheVO>();
			for (Tache t : list) {
				result.add(new TacheVO(t));
			}
		}

		return result;
	}

	@Override
	public TacheVO searchById(long id) {
		TacheVO tache = null;
		
		Tache t = dao.searchById(id);
		
		if (t != null) {
			tache = new TacheVO(t);
		}
		
		return tache;
	}

	@Override
	public void updateTache(TacheVO t) {
		dao.update(t.convertToEntitie());
		
	}
	
}

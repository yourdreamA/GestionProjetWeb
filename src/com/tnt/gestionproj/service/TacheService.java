package com.tnt.gestionproj.service;

import java.util.List;

import com.tnt.gestionproj.vo.DetailsTacheVO;
import com.tnt.gestionproj.vo.TacheVO;

public interface TacheService {

	String addTache(TacheVO tache) throws Exception;

	List<TacheVO> getTaches(TacheVO filter);

	void updateTache(TacheVO t);

	TacheVO searchById(long id);
	
	String addDetailsTache(DetailsTacheVO det) throws Exception;

}

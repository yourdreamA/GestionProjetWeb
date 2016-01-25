package com.tnt.gestionproj.dao;

import java.util.List;

import com.tnt.gestionproj.entities.DetailsTache;
import com.tnt.gestionproj.entities.Tache;
import com.tnt.gestionproj.vo.TacheVO;

public interface TacheDao {

	List<Tache> getAll();
	void add(Tache tache);
	Tache searchById(long id);
	void update(Tache tache);
	List<Tache> getTaches(TacheVO filter);
	void addDetails(DetailsTache det);
}

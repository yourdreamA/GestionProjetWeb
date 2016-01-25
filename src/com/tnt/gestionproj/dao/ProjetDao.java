package com.tnt.gestionproj.dao;

import java.util.List;

import com.tnt.gestionproj.entities.Projet;
import com.tnt.gestionproj.vo.ProjetVO;

public interface ProjetDao {

	List<Projet> getAll();
	void add(Projet projet);
	Projet searchById(long id);
	void update(Projet projet);
	List<Projet> getProjets(ProjetVO filter);
}

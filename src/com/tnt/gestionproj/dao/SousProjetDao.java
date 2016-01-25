package com.tnt.gestionproj.dao;

import java.util.List;

import com.tnt.gestionproj.entities.SousProjet;
import com.tnt.gestionproj.vo.SousProjetVO;

public interface SousProjetDao {
	SousProjet update(SousProjet sousProjet);
	List<SousProjet> getSousProjets(long idProjet);
	List<SousProjet> getSousProjets(SousProjetVO filter);
}

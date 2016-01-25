package com.tnt.gestionproj.service;

import java.util.List;

import com.tnt.gestionproj.vo.ProjetVO;

public interface ProjetService {

	ProjetVO addProjet(ProjetVO projet);

	List<ProjetVO> getProjets(ProjetVO filter);

	ProjetVO searchById(long id);

	void updateProjet(ProjetVO projet);

	boolean existsProjet(String nomProjet);


	ProjetVO searchProjetForGantt(long id);

}

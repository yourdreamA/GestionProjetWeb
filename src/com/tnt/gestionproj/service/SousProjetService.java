package com.tnt.gestionproj.service;

import java.util.List;

import com.tnt.gestionproj.vo.SousProjetVO;

public interface SousProjetService {

	String enregistrerStructureProjet(List<SousProjetVO> projet) throws Exception;

	SousProjetVO getStructureProjet(long idProjet) throws Exception;
	
	List<SousProjetVO> getSousProjets(SousProjetVO filter) throws Exception;

	String validateStructureProjet(List<SousProjetVO> listSousProjet)
			throws Exception;
}

package com.tnt.gestionproj.app.impl;

import java.util.List;

import org.jbpm.task.query.TaskSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tnt.gestionproj.entities.Tache;
import com.tnt.gestionproj.jbpm.KnowledgeSessionProvider;
import com.tnt.gestionproj.jbpm.KnowledgeTaskProvider;
import com.tnt.gestionproj.service.TacheService;
import com.tnt.gestionproj.vo.DetailsTacheVO;
import com.tnt.gestionproj.vo.TacheVO;

@Component("tacheApp")
public class TacheAppImpl extends BaseAppImpl {

	@Autowired
	private TacheService tacheService;
	
	@Autowired
	private KnowledgeSessionProvider ksessionProvider;
	
	@Autowired
	private KnowledgeTaskProvider taskProvider;
	
	public List<TacheVO> getTaches(TacheVO filter) throws Exception {
		List<TacheVO> list = null;
		try {
			list = tacheService.getTaches(filter);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		
		if (list != null && !list.isEmpty()) {
			for (TacheVO tacheVO : list) {
				
				if (tacheVO != null) {
					tacheVO.setStatus(getStausTache(tacheVO));
				}
				
			}
		}
		
		return list;
	}
	
	private String getStausTache(TacheVO tacheVO) throws Exception {
		String status = "Affecté";
		
		if (tacheVO != null) {
			if (tacheVO.getProgression() > 0) {
				if (tacheVO.getProgression() == 100) {
					status = "Terminé";
				} else {
					status = "En cours";
				}
			} else {
				//en attente(affecté and non started)
				if (tacheVO.getExecuteurs() == null || tacheVO.getExecuteurs().isEmpty()) {
					status = "Non affecté";
				} else {
					String user = tacheVO.getExecuteurs().get(0).getLogin().getUsername();
					
					List<TaskSummary> tasks = taskProvider.getTaskService().getTasksAssignedAsPotentialOwner(
							user, "en-UK");
					
					TaskSummary task = getTask(tasks, "Perform the task", tacheVO.getId());
					if (task != null) {
						status = "En attente";
					}
				}
			}
		}
		
		return status;
	}
	
	public String addDetailsTache(DetailsTacheVO det) throws Exception {
		TacheVO tacheVO = det.getTache();//tacheService.searchById(det.getTache().getId());
		String chef = det.getExecuteur().getLogin().getUsername();
		
		List<TaskSummary> tasks = taskProvider.getTaskService().getTasksAssignedAsPotentialOwner(
				chef, "en-UK");
		
		TaskSummary task = getTask(tasks, "Perform the task", tacheVO.getId());
		
		//premier details ?
		if (tacheVO.getListDetails() == null || det.getTache().getListDetails().isEmpty()) {
			if (task != null) {
				taskProvider.getTaskService().start(task.getId(), chef);
			}
		}
		
		if (det.getAvancement() == 100) {
			//Task 1
			if (task != null) {
				taskProvider.getTaskService().complete(task.getId(), chef, null);
			}
			
			//Task 2
			tasks = taskProvider.getTaskService().getTasksAssignedAsPotentialOwner(
					chef, "en-UK");
			
			TaskSummary task2 = getTask(tasks, "Add detail + progression", tacheVO.getId());
			if (task2 != null) {
				taskProvider.getTaskService().complete(task2.getId(), chef, null);
			}
			
			//Task 3
			tasks = taskProvider.getTaskService().getTasksAssignedAsPotentialOwner(
					chef, "en-UK");
			
			TaskSummary task3 = getTask(tasks, "Validate End Task", tacheVO.getId());
			if (task3 != null) {
				taskProvider.getTaskService().complete(task3.getId(), chef, null);
			}
			
			
		} else {
			
		}
		
		//ajout details de la tache
		tacheService.addDetailsTache(det);
		
		return "OK";
	}
	
	public TacheVO searchById(long id) throws Exception {
		TacheVO tache = tacheService.searchById(id);
		
		
		if (tache != null) {
			tache.setStatus(getStausTache(tache));
		}
		
		return tache;
	}
}

package com.tnt.gestionproj.app.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.process.core.datatype.DataType;
import org.drools.process.core.datatype.impl.NewInstanceDataTypeFactory;
import org.drools.process.core.datatype.impl.type.IntegerDataType;
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.ruleflow.core.factory.ActionNodeFactory;
import org.jbpm.ruleflow.core.factory.EndNodeFactory;
import org.jbpm.ruleflow.core.factory.JoinFactory;
import org.jbpm.ruleflow.core.factory.SplitFactory;
import org.jbpm.ruleflow.core.factory.StartNodeFactory;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.jbpm.task.Status;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.workflow.core.node.Join;
import org.jbpm.workflow.core.node.Split;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tnt.gestionproj.jbpm.KnowledgeBaseProducer;
import com.tnt.gestionproj.jbpm.KnowledgeSessionProvider;
import com.tnt.gestionproj.jbpm.KnowledgeTaskProvider;
import com.tnt.gestionproj.service.SousProjetService;
import com.tnt.gestionproj.service.TacheService;
import com.tnt.gestionproj.vo.SousProjetVO;
import com.tnt.gestionproj.vo.TacheVO;

@Component("sousProjetApp")
public class SousProjetAppImpl extends BaseAppImpl {

	@Autowired
	private TacheService tacheService;
	
	@Autowired
	private SousProjetService sousProjetService;
	
	@Autowired
	private KnowledgeSessionProvider ksessionProvider;
	
	@Autowired
	private KnowledgeTaskProvider taskProvider;
	
	@Autowired
	private KnowledgeBaseProducer knowledgeBaseProducer;
	
	public String launchSubProject(SousProjetVO sousProjet) throws Exception {
		List<Status> statusProg = new ArrayList<Status>();
		statusProg.add(Status.InProgress);
		String chef = sousProjet.getChefSousProjet().getLogin().getUsername();
		
		List<TaskSummary> tasks = taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(
				chef, statusProg, "en-UK");
		
		TaskSummary task = getTask(tasks, "Structure subproject", sousProjet.getId());
		if (task != null) {
			taskProvider.getTaskService().complete(task.getId(), chef, null);
		}
		
		return "OK";
	}
	
	public TacheVO addTask(SousProjetVO sousProjet, TacheVO tache) throws Exception {

		tacheService.addTache(tache);
		
		TacheVO filter = new TacheVO();
		filter.setSousProjet(new SousProjetVO());
		filter.getSousProjet().setId(sousProjet.getId());
		List<TacheVO> listTache = tacheService.getTaches(filter);
		
		try {
			generateStructureSousProjet(sousProjet, listTache);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		//release tasks
		String chef = sousProjet.getChefSousProjet().getLogin().getUsername();
		
		List<TaskSummary> tasks = 
				taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(chef, status, "en-UK");

		//1- Structure subproject
		TaskSummary task = getTask(tasks, "Structure subproject", sousProjet.getId());
		if (task != null) {
			taskProvider.getTaskService().start(task.getId(), chef);
			System.out.println("22222222222222222222222" + sousProjet.getNom() + " : ajout tache + diagramme effectué avec succès.");
			//taskProvider.getTaskService().complete(task.getId(), chef, null);
		} else {
			List<Status> statusProg = new ArrayList<Status>();
			statusProg.add(Status.InProgress);
			tasks = 
					taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(
							chef, statusProg, "en-UK");
			System.out.println("33333333333333333333333" + sousProjet.getNom() + " : ajout tache + MAJ diagramme effectué avec succès.");
			task = getTask(tasks, "Structure subproject", sousProjet.getId());
			if (task != null) {
				
			} else {
				List<Status> statusComp = new ArrayList<Status>();
				statusComp.add(Status.Completed);
				tasks = 
						taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(
								chef, statusComp, "en-UK");
				if (task != null) {
					throw new Exception(sousProjet.getNom() + " : sous projet déjà validé. structuration impossible.");
				} else {
					throw new Exception(chef + " ne peut pas modifier le sous projet : " + sousProjet.getNom());
				}
				
			}
			
		}
		
		
		tasks = 
				taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(chef, status, "en-UK");

		//2- Monitoring subproject
		task = getTask(tasks, "Monitoring subproject", sousProjet.getId());
		if (task != null) {
			taskProvider.getTaskService().start(task.getId(), chef);
			System.out.println("___________________________start Monitoring subproject " + sousProjet.getNom() + ", chef : " + chef);
		}
				
		return tache;
		
	}
	
	public List<SousProjetVO> getSousProjets(SousProjetVO filter) throws Exception {
		List<SousProjetVO> list = sousProjetService.getSousProjets(filter);

		if (list != null && !list.isEmpty()) {
			for (SousProjetVO p : list) {
				/*String status = "interdite";
				
				String user = p.getChefSousProjet().getLogin().getUsername();
				
				List<TaskSummary> tasks = taskProvider.getTaskService().getTasksAssignedAsPotentialOwner(
						user, "en-UK");
				
				TaskSummary task = getTask(tasks, "Structure subproject", p.getId());
				
				if (task != null) {
					if (Status.Created == task.getStatus()) {
						status = "Créé";//rouge
					} else if (Status.Reserved == task.getStatus()) {
						status = "Affecté";//jaune
					} else if (Status.InProgress == task.getStatus()) {
						status = "Structuration en cours";//vert
					}
				} else {
					TaskSummary taskM = getTask(tasks, "Monitoring subproject", p.getId());
					
					if (taskM != null) {
						status = "Réalisation en cours";
					} else {
						status = "Terminé";
					}
				}*/
				
				p.setStatus(getStausSp(p));
				
			}
		}

		return list;
	}
	
	private String getStausSp(SousProjetVO sp) throws Exception {
		String status = "Affecté";
		
		TacheVO filter = new TacheVO();
		SousProjetVO spf = new SousProjetVO();
		spf.setId(sp.getId());
		filter.setSousProjet(spf);
		List<TacheVO> listTache = tacheService.getTaches(filter );
		
		if (sp != null) {
			if (listTache == null || listTache.isEmpty()) {
				
			} else {
				int nbrTermine = 0;
				for (TacheVO t : listTache) {
					String statusTache = getStausTache(t);
					if ("En cours".equals(statusTache) || "En attente".equals(statusTache)
							|| "Non affecté".equals(statusTache)) {
						status = "Réalisation tache en cours";
						break;
					} else if ("Affecté".equals(statusTache)) {
						status = "Structuration en cours";
					} else {
						nbrTermine++;
					}
				}
				if (nbrTermine == listTache.size()) {
					status = "Terminé";
				}
			}
		}
		return status;
	}
	
	private void generateStructureSousProjet(SousProjetVO sousProjet, List<TacheVO> listTache) throws Exception {
		String nomProjet = sousProjet.getProjet().getNom();
		int id = 1;
		RuleFlowProcessFactory factory =

				RuleFlowProcessFactory.createProcess("com.tnt.bpmn.subProjectRealization_" + nomProjet);

		StartNodeFactory start = factory.startNode(id++).name("Start");
		start.done();
		
		DataType dt = new NewInstanceDataTypeFactory(StringDataType.class).createDataType();
		DataType dtInt = new NewInstanceDataTypeFactory(IntegerDataType.class).createDataType();
		factory.variable("projectName", dt, nomProjet);
		factory.variable("progression", dtInt, 0);
		
		ActionNodeFactory updateActorTask = factory.actionNode(id).name("update actors");
		
		String action = "";
		
		for (TacheVO tache : listTache) {
			String actor = tache.getExecuteurs().get(0).getLogin().getUsername();
			action += "kcontext.setVariable(\"executeur_" + tache.getId()
					+ "\",\"" + actor
					+ "\");\n";
			action += "kcontext.setVariable(\"idTache_" + tache.getId()
					+ "\",\"" + tache.getId()
					+ "\");\n";
		}
		updateActorTask.action("java", action);
		
		updateActorTask.done();
		factory.connection(id - 1, id);
		id++;
		
		Map<String, List<TacheVO>> mapParallel = new HashMap<String, List<TacheVO>>();

		for (TacheVO tache : listTache) {
			String exec = tache.getExecuteurs().get(0).getLogin().getUsername();
			List<TacheVO> parallelTaches = new ArrayList<TacheVO>();
			if (mapParallel.containsKey(exec)) {
				parallelTaches = mapParallel.get(exec);
			}
			
			parallelTaches.add(tache);
			mapParallel.put(exec, parallelTaches);
		}
		
		int idJoinDeb = 0;
		int idJoinFin = 0;
		if (mapParallel.size() > 1) {
			//join debut
			idJoinDeb = id;
			//diverging
			SplitFactory split = factory.splitNode(idJoinDeb).type(Split.TYPE_AND);
			split.done();
			factory.connection(id - 1, id);
			id++;
			
			//join fin
			idJoinFin = id;
			//converging
			JoinFactory joinFin = factory.joinNode(idJoinFin).type(Join.TYPE_AND);
			joinFin.done();
			id++;
		}
		
		for (String actor : mapParallel.keySet()) {
			
			List<TacheVO> list = mapParallel.get(actor);
			int sizeList = list.size();
			boolean first = true;
			boolean last = false;
			int count = 1;
			for (TacheVO tache : list) {
				last = (count == sizeList);
				factory.variable("executeur_" + tache.getId(), dt, actor);
				factory.variable("idTache_" + tache.getId(), dt, String.valueOf(tache.getId()));

				//////////////////////////////TASK   //////////////////////////////////
				SubProcessNodeFactory call = factory.subProcessNode(id).name(tache.getNom())
						.processId("com.tnt.bpmn.taskProcess");
				call.inMapping("progression", "progression");
				call.inMapping("executeur", "executeur_" + tache.getId());
				call.inMapping("idTache", "idTache_" + tache.getId());
				call.done();
				//////////////////////////////////////////////////////////////////////////
				if (first && last) {
					if (idJoinDeb > 0) {
						factory.connection(idJoinDeb, id);
						factory.connection(id, idJoinFin);
					} else {
						factory.connection(id - 1, id);
					}
				} else if (first) {
					if (idJoinDeb > 0) {
						factory.connection(idJoinDeb, id);
					} else {
						factory.connection(id - 1, id);
					}
					first = false;
				} else if (last) {
					if (idJoinFin > 0) {
						factory.connection(id - 1, id);
						factory.connection(id, idJoinFin);
					} else {
						factory.connection(id - 1, id);
					}
				} else {
					factory.connection(id - 1, id);
				}
				
				id++;
				count++;
			}
		}
		
		
		

		EndNodeFactory end = factory.endNode(id).name("End");
		end.done();
		
		if (idJoinFin > 0) {
			factory.connection(idJoinFin, id);
		} else {
			factory.connection(id - 1, id);
		}
		

		factory
		// Header
		.name("subProjectRealization_" + nomProjet)
		.version("1.0")
		.packageName("com.tnt.bpmn");

		RuleFlowProcess process = factory.validate().getProcess();

		ClassLoader[] classLoaders = { getClass().getClassLoader() };
		//ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoaders[0].getResource("com/tnt/gestionproj/jbpm/process");

		File file = new File(url.toURI().getPath() + "/" + "subProjectRealization_" + nomProjet + ".bpmn");

		file.createNewFile();

		String content = XmlBPMNProcessDumper.INSTANCE.dump(process, XmlBPMNProcessDumper.NO_META_DATA);

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();

		knowledgeBaseProducer.addResource("subProjectRealization_" + nomProjet + ".bpmn");


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
}

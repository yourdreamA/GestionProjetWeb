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
import org.drools.process.core.datatype.impl.type.StringDataType;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemHandler;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.ruleflow.core.factory.ActionNodeFactory;
import org.jbpm.ruleflow.core.factory.EndNodeFactory;
import org.jbpm.ruleflow.core.factory.StartNodeFactory;
import org.jbpm.ruleflow.core.factory.SubProcessNodeFactory;
import org.jbpm.task.Status;
import org.jbpm.task.query.TaskSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tnt.gestionproj.jbpm.KnowledgeBaseProducer;
import com.tnt.gestionproj.jbpm.KnowledgeSessionProvider;
import com.tnt.gestionproj.jbpm.KnowledgeTaskProvider;
import com.tnt.gestionproj.service.ProjetService;
import com.tnt.gestionproj.service.SousProjetService;
import com.tnt.gestionproj.vo.ProjetVO;
import com.tnt.gestionproj.vo.SousProjetVO;

@Component("projetApp")
public class ProjetAppImpl extends BaseAppImpl {

	@Autowired
	private ProjetService projetService;

	@Autowired
	private SousProjetService sousProjetService;
	
	@Autowired
	private KnowledgeSessionProvider ksessionProvider;
	
	@Autowired
	private KnowledgeTaskProvider taskProvider;
	
	@Autowired
	private KnowledgeBaseProducer knowledgeBaseProducer;
	

	public ProjetVO addProjet(ProjetVO projet) throws Exception {
		
		//TODO validate
		if (projetService.existsProjet(projet.getNom())) {
			throw new Exception(projet.getNom() + " existe déjà.");
		}
		if (projet.getAdministrateur() == null
				|| projet.getAdministrateur().getLogin() == null) {
			throw new Exception("Administrateur est obligatoir.");
		}
		if (projet.getChefProjet() == null
				|| projet.getChefProjet().getLogin() == null) {
			throw new Exception("Chef du projet est obligatoir.");
		}
		
		projet = projetService.addProjet(projet);
		
		String admin = projet.getAdministrateur().getLogin().getUsername();
		//create process
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("projectName", projet.getNom());
		params.put("admin", admin);
		params.put("chefProject", projet.getChefProjet().getLogin().getUsername());
		params.put("user", null);
		params.put("projectRealization_id", "com.tnt.bpmn.projectRealization_" + projet.getNom());
		params.put("subProjectRealization_id", "com.tnt.bpmn.subProjectRealization_" + projet.getNom());
		params.put("idProjet", "" + projet.getId());
		//generate process project and bpmn
		createProcessProject(params);
		
		//lancer processus
		StatefulKnowledgeSession ksession = ksessionProvider.getKsession();
		
//		WorkItemHandler workItemHandler = new ServiceTaskHandler(ksession);
		WorkItemHandler workItemHandler = taskProvider.getLocalHTWorkItemHandler();
		ksession
				.getWorkItemManager()
				.registerWorkItemHandler("Human Task",
						workItemHandler);
		
		//start process
		ProcessInstance instance = ksession.startProcess(
				"com.tnt.bpmn.projectProcess_" + projet.getNom(), params);
		projet.setIdInstance(instance.getId());
		
		ksessionProvider.put(projet.getNom(), instance);
//		ksessionProvider.getKsession().fireAllRules();
		
		//release tasks admin
		List<TaskSummary> tasks = 
				taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(admin, status, "en-UK");
		
		//1- Add project
		TaskSummary task = getTask(tasks, "Add project", projet.getId());
		if (task != null) {
			taskProvider.getTaskService().start(task.getId(), admin);
			//projet = projetService.addProjet(projet);
			taskProvider.getTaskService().complete(task.getId(), admin, null);
		} else {
			throw new Exception("pas de tache 'Add project' en attente.");
		}
		
		tasks = 
				taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(admin, status, "en-UK");
		
		//2- Select user
		task = getTask(tasks, "Select user", projet.getId());
		if (task != null) {
			taskProvider.getTaskService().start(task.getId(), admin);
			//
			taskProvider.getTaskService().complete(task.getId(), admin, null);
		} else {
			throw new Exception("pas de tache 'Select user' en attente.");
		}
		
		tasks = 
				taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(admin, status, "en-UK");
		
		//3- Affect project
		task = getTask(tasks, "Affect project", projet.getId());
		if (task != null) {
			taskProvider.getTaskService().start(task.getId(), admin);
			//
			taskProvider.getTaskService().complete(task.getId(), admin, null);
		} else {
			throw new Exception("pas de tache 'Affect project' en attente.");
		}
		
		tasks = 
				taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(admin, status, "en-UK");
		
		//4- Validate add project
		task = getTask(tasks, "Validate add project", projet.getId());
		if (task != null) {
			taskProvider.getTaskService().start(task.getId(), admin);
			//
			taskProvider.getTaskService().complete(task.getId(), admin, null);
		} else {
			throw new Exception("pas de tache 'Validate add project' en attente.");
		}
		
		return projet;
		
	}


	public String enregistrerStructureProjet(long idProjet, List<SousProjetVO> listSousProjet) throws Exception {
		String error = sousProjetService.validateStructureProjet(listSousProjet);
		if (error != null && !error.isEmpty()) {
			return error;
		}

		ProjetVO projet = null;
		String chef = null;
		if (listSousProjet != null && !listSousProjet.isEmpty()) {
			projet = listSousProjet.get(0).getProjet();
			chef = projet.getChefProjet().getLogin().getUsername();
		}
		
		sousProjetService.enregistrerStructureProjet(listSousProjet);
		SousProjetVO sp = sousProjetService.getStructureProjet(idProjet);

		try {
			createProcessProjectRealization(sp);
		} catch (Exception e) {
			throw e;
		}
		
		//release tasks
		List<TaskSummary> tasks = 
				taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(chef, status, "en-UK");
		
		//1- Structure project
		TaskSummary task = getTask(tasks, "Structure project", projet.getId());
		if (task != null) {
			taskProvider.getTaskService().start(task.getId(), chef);

			

			taskProvider.getTaskService().complete(task.getId(), chef, null);
		} else {
			throw new Exception("pas de tache 'Structure project' en attente.");
		}
		
		
		tasks = 
				taskProvider.getTaskService().getTasksAssignedAsPotentialOwnerByStatus(chef, status, "en-UK");
		
		//2- Monitoring project
		task = getTask(tasks, "Monitoring project", projet.getId());
		if ("Monitoring project".equals(task.getName())) {
			taskProvider.getTaskService().start(task.getId(), chef);
			System.out.println("___________________________start Monitoring project");
		}

		return "OK";
	}

	
	public List<ProjetVO> getProjets(ProjetVO filter) throws Exception {
		List<ProjetVO> list = projetService.getProjets(filter);

		if (list != null && !list.isEmpty()) {
			for (ProjetVO p : list) {
				String status = "interdite";
				
				String user = p.getChefProjet().getLogin().getUsername();
				
				List<TaskSummary> tasks = taskProvider.getTaskService().getTasksAssignedAsPotentialOwner(
						user, "en-UK");
				
				TaskSummary task = getTask(tasks, "Structure project", p.getId());
				if (task != null) {
					if (Status.Created == task.getStatus()) {
						status = "Créé";//rouge
					} else if (Status.Reserved == task.getStatus()) {
						status = "Affecté";//jaune
					} else if (Status.InProgress == task.getStatus()) {
						status = "Structuration en cours";//bleu
					}
				} else {
					TaskSummary taskM = getTask(tasks, "Monitoring project", p.getId());
					
					if (taskM != null) {
						status = "Réalisation en cours";
					} else {
						status = "Terminé";
					}
				}
				
				p.setStatus(status);
				
			}
		}

		return list;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////    cretae bpmn ////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////
	
	private void createProcessProjectRealization(SousProjetVO sousProjetStructure) throws Exception {
		String nomProjet = sousProjetStructure.getProjet().getNom();
		int id = 1;
		RuleFlowProcessFactory factory =

				RuleFlowProcessFactory.createProcess("com.tnt.bpmn.projectRealization_" + nomProjet);

		StartNodeFactory start = factory.startNode(id++).name("Start");
		start.done();
		
		DataType dt = new NewInstanceDataTypeFactory(StringDataType.class).createDataType();
		factory.variable("projectName", dt, nomProjet);
		factory.variable("subProjectRealization_id", dt, "com.tnt.bpmn.subProjectRealization_" + nomProjet);
		
		
		//
		ActionNodeFactory updateActorTask = factory.actionNode(id).name("update actors");

		/////////////////////////////////////////////////////////////
		List<SousProjetVO> listSp = new ArrayList<SousProjetVO>();
		SousProjetVO sp = sousProjetStructure;
		while(sp.getSousProjetSuivant() != null) {
			listSp.add(sp);
			sp = sp.getSousProjetSuivant();
		}
		listSp.add(sp);
		////////////////////////////////////////////////////////////
		if (!listSp.isEmpty()) {
			
			String action = "";
//			action += "kcontext.setVariable(\"subProjectRealization_id"
//					+ "\",\"com.tnt.bpmn.subProjectRealization_" + nomProjet
//					+ "\");\n";
//			action += "System.out.println(\"+-+-+-+-+-+-+-+-+-subProjectRealization_id=\" + subProjectRealization_id);\n";
					
			for (SousProjetVO sousProjetVO : listSp) {
				action += "kcontext.setVariable(\"chefSousProjet_" + sousProjetVO.getNom()
						+ "\",\"" + sousProjetVO.getChefSousProjet().getLogin().getUsername()
						+ "\");\n";
				action += "kcontext.setVariable(\"idSousProjet_" + sousProjetVO.getNom()
						+ "\",\"" + sousProjetVO.getId()
						+ "\");\n";
			}
			updateActorTask.action("java", action);
			
			updateActorTask.done();
			factory.connection(id - 1, id);
			id++;
			
			for (SousProjetVO sousProjetVO : listSp) {
				factory.variable(
						"chefSousProjet_" + sousProjetVO.getNom(), 
						dt, 
						sousProjetVO.getChefSousProjet().getLogin().getUsername());
				factory.variable(
						"idSousProjet_" + sousProjetVO.getNom(), 
						dt, 
						String.valueOf(sousProjetVO.getId()));
				//kcontext.setVariable("chefSousProjet_" + sousProjetVO.getNom(), sousProjetVO.getChefSousProjet().getLogin().getUsername());
				
				SubProcessNodeFactory call = factory.subProcessNode(id).name(sousProjetVO.getNom())
						.processId("com.tnt.bpmn.subProjectProcess");
				
				call.inMapping("projectName", "projectName");
				call.inMapping("chefSousProjet", "chefSousProjet_" + sousProjetVO.getNom());
				call.inMapping("idSousProjet", "idSousProjet_" + sousProjetVO.getNom());
				call.inMapping("subProjectRealization_id", "subProjectRealization_id");
				
				call.done();
				
				factory.connection(id - 1, id);
				id++;
			}
		}

		EndNodeFactory end = factory.endNode(id).name("End");
		end.done();
		factory.connection(id - 1, id);

		factory
		// Header
		.name("projectRealization_" + nomProjet)
		.version("1.0")
		.packageName("com.tnt.bpmn");

		RuleFlowProcess process = factory.validate().getProcess();

		ClassLoader[] classLoaders = { getClass().getClassLoader() };
		//ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoaders[0].getResource("com/tnt/gestionproj/jbpm/process");

		String urlFile = url.toURI().getPath() + "/" + "projectRealization_" + nomProjet + ".bpmn";
		File file = new File(urlFile);

		// if file doesnt exists, then create it
//		if (!file.exists()) {
			file.createNewFile();
//		}

		String content = XmlBPMNProcessDumper.INSTANCE.dump(process, XmlBPMNProcessDumper.NO_META_DATA);

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();

		knowledgeBaseProducer.addResource("projectRealization_" + nomProjet + ".bpmn");
		//ksessionProvider.upgradeProcessInstance("projectRealization_" + nomProjet + ".bpmn", "com.tnt.bpmn.projectRealization_" + nomProjet, nomProjet);

	}
	
	/**
	 * createProcessProject.
	 * @param params
	 * @throws Exception
	 */
	private void createProcessProject(Map<String, Object> params) throws Exception {
		
		String nomProjet = (String) params.get("projectName");
		int id = 1;
		RuleFlowProcessFactory factory =

				RuleFlowProcessFactory.createProcess("com.tnt.bpmn.projectProcess_" + nomProjet);

		DataType dt = new NewInstanceDataTypeFactory(StringDataType.class).createDataType();
		factory.variable("projectName", dt, params.get("projectName"));
		factory.variable("admin", dt, params.get("admin"));
		factory.variable("chefProject", dt, params.get("chefProject"));
		factory.variable("user", dt, null);
		factory.variable("projectRealization_id", dt, "com.tnt.bpmn.projectRealization_" + nomProjet);
		factory.variable("subProjectRealization_id", dt, "com.tnt.bpmn.subProjectRealization_" + nomProjet);
		factory.variable("idProjet", dt,  params.get("idProjet"));
		
		StartNodeFactory start = factory.startNode(id++).name("Start");
		start.done();

		SubProcessNodeFactory call = factory.subProcessNode(id).name(nomProjet)
				.processId("com.tnt.bpmn.projectProcess");
		//call.inMapping("projectName", "projectName");
		call.inMapping("admin", "admin");
		call.inMapping("user", "user");
		call.inMapping("chefProject", "chefProject");
		call.inMapping("projectRealization_id", "projectRealization_id");
		call.inMapping("subProjectRealization_id", "subProjectRealization_id");
		call.inMapping("projectName", "projectName");
		call.inMapping("idProjet", "idProjet");
		
		call.done();
		factory.connection(id - 1, id);
		id++;

		EndNodeFactory end = factory.endNode(id).name("End");
		end.done();
		factory.connection(id - 1, id);

		factory
		// Header
		.name(nomProjet)
		.version("1.0")
		.packageName("com.tnt.bpmn");

		RuleFlowProcess process = factory.validate().getProcess();

		ClassLoader[] classLoaders = { getClass().getClassLoader() };
		//ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		URL url = classLoaders[0].getResource("com/tnt/gestionproj/jbpm/process");

		File file = new File(url.toURI().getPath() + "/" + nomProjet + ".bpmn");

		// if file doesnt exists, then create it
//		if (!file.exists()) {
			file.createNewFile();
//		}

		String content = XmlBPMNProcessDumper.INSTANCE.dump(process, XmlBPMNProcessDumper.NO_META_DATA);

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();
		
		knowledgeBaseProducer.addResource(nomProjet + ".bpmn");

	}
}

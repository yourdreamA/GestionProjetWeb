package com.tnt.gestionproj.app.impl;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.task.Status;
import org.jbpm.task.query.TaskSummary;

public abstract class BaseAppImpl {

	protected List<Status> status = new ArrayList<Status>();
	
	public BaseAppImpl() {
		status.add(Status.Created);
		status.add(Status.Reserved);
	}
	
	
//	protected TaskSummary getTask(List<TaskSummary> tasks, String taskName) throws Exception {
//		if (tasks != null && !tasks.isEmpty()) {
//			for (TaskSummary task : tasks) {
//				if (taskName.equals(task.getName())) {
//					return task;
//				}
//			}
//		}
//		
//		
//		return null;
//	}
	
	protected TaskSummary getTask(List<TaskSummary> tasks, String taskName, long id) throws Exception {
		if (tasks != null && !tasks.isEmpty()) {
			for (TaskSummary task : tasks) {
				if (taskName.equals(task.getName())
						&& String.valueOf(id).equals(task.getDescription())) {
					return task;
				}
			}
		}
		
		
		return null;
	}
}

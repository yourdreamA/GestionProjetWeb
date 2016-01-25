package com.tnt.gestionproj.jbpm.workitem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.drools.process.instance.WorkItemHandler;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemManager;

public class SpringInvokeWorkItemHandler implements WorkItemHandler {	
	private String i;
	private Object instance;
	
	
	public SpringInvokeWorkItemHandler() {
		// TODO Auto-generated constructor stub
	}
	
	
	public SpringInvokeWorkItemHandler(String i, Object instance) {
		super();
		this.i = i;
		this.instance = instance;
	}

	private Object parameter;

	
	@Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String operation = (String) workItem.getParameter("Operation");
		String parameterType = (String) workItem.getParameter("ParameterType");
		Object parameter = workItem.getParameter("Parameter");
		
		if (workItem.getParameter("Interface") != null) {
			i = (String) workItem.getParameter("Interface");
		}
		if (workItem.getParameter("Bean") != null) {
			instance = (Object) workItem.getParameter("Bean");
		}
        
        try {
            Class<?> c = Class.forName(i);
            Class<?>[] classes = null;
            Object[] params = null;
            if (parameterType != null) {
                classes = new Class<?>[] {
                    Class.forName(parameterType)
                };
                params = new Object[] {
                    parameter
                };
            }
            Method method = c.getMethod(operation, classes);
            Object result = method.invoke(instance, params);
            Map<String, Object> results = new HashMap<String, Object>();
            results.put("Result", result);
            manager.completeWorkItem(workItem.getId(), results);
        } catch (ClassNotFoundException e) {
            System.err.println(e);
        } catch (IllegalAccessException e) {
            System.err.println(e);
        } catch (NoSuchMethodException e) {
            System.err.println(e);
        } catch (InvocationTargetException e) {
            System.err.println(e);
        }
    }

    @Override
    public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
        // TODO Auto-generated method stub
        System.out.println("**************************** CustomWorkItemHandler abortWorkItem" + workItem.getProcessInstanceId());
    }
    
    public void setParameter(Object parameter) {
		this.parameter = parameter;
	}

}

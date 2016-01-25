package com.tnt.gestionproj.common;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanInitializer {

	private static ApplicationContext appContext = null;

	public static ApplicationContext getApplicationContext() {

		try {
			if (appContext == null) {
				appContext = new ClassPathXmlApplicationContext(
						"applicationContextJPA.xml");
			}
			return appContext;

		} catch (BeansException e) {
			throw e;
		}

	}

	public static Object getBean(String serviceName) {

		return getApplicationContext().getBean(serviceName);
	}

}

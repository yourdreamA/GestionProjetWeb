package com.tnt.gestionproj.test;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jbpm.task.service.mina.MinaTaskServer;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.tnt.gestionproj.app.impl.TacheAppImpl;
import com.tnt.gestionproj.jbpm.KnowledgeTaskServiceSession;
import com.tnt.gestionproj.vo.SousProjetVO;
import com.tnt.gestionproj.vo.TacheVO;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TacheAppTest {

	private static ClassPathXmlApplicationContext context;

	private static TacheAppImpl tacheAppImpl;
	
	@BeforeClass
	public static void initHibernate() throws Exception {
		System.setProperty("java.naming.factory.initial",
				"bitronix.tm.jndi.BitronixInitialContextFactory");
		
		context = new ClassPathXmlApplicationContext("applicationContext_test.xml");
		
		tacheAppImpl = (TacheAppImpl) context.getBean("tacheApp");
		
		KnowledgeTaskServiceSession taskServiceSession = (KnowledgeTaskServiceSession) context.getBean("knowledgeTaskServiceSession");
		MinaTaskServer server = new MinaTaskServer(taskServiceSession.getTaskService());
		Thread thread = new Thread(server);
		thread.start();
	}
	
	@Test
	public void getTaches() {
		try {
			List<TacheVO> list = tacheAppImpl.getTaches(new TacheVO());
			if (list != null && !list.isEmpty()) {
				for (TacheVO tacheVO : list) {
					System.out.println(tacheVO);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}
		
	}
}

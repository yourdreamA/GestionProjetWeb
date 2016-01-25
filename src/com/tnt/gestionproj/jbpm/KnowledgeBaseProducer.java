package com.tnt.gestionproj.jbpm;

import java.io.File;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.Resource;
import org.drools.io.ResourceFactory;
import org.springframework.stereotype.Component;

@Component
public class KnowledgeBaseProducer implements Serializable {

	// @Produces
	// @Named
	// @KBase(value = "kbase")
	// @ApplicationScoped
	
	private KnowledgeBase kbase;
	public KnowledgeBase getKbase() {
		return kbase;
	}

	private KnowledgeBuilder kbuilder;
	
	public KnowledgeBase addResource(String name) {
		if (kbuilder == null || kbase == null) {
			produceKnowledgeBase();
			return kbase;
		}
		
		Resource resource = getBpmn(name);
		if (resource == null) {
			System.out.println("***********----------------problem");
		}
		
		//ClassLoader[] classLoaders = { getClass().getClassLoader() }; // Custom class loader
		
		//KnowledgeBuilderConfiguration kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, classLoaders);

		kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbase);

		kbuilder.add(resource, ResourceType.BPMN2);
		
//		KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, classLoaders);
//		kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
    	kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
    	
    	return kbase;
	}
	
	
	public KnowledgeBase produceKnowledgeBase() {

		
		
//		List<Resource> rules = loadRules();
//		
//		for (Resource resource : rules) {
//			kbuilder.add(resource, ResourceType.DRL);
//		}
		
		ClassLoader[] classLoaders = { getClass().getClassLoader() }; // Custom class loader

		
		KnowledgeBaseConfiguration kconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration(null, classLoaders);

		kbuilder = produceKnowledgeBuilder(classLoaders);
		//Create a knowledge base and add the generated package
        kbase = KnowledgeBaseFactory.newKnowledgeBase(kconf);
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		

		return kbase;
	}
	
	private KnowledgeBuilder produceKnowledgeBuilder(ClassLoader[] classLoaders) {
		
		KnowledgeBuilderConfiguration kbuilderConf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration(null, classLoaders);

		kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(kbuilderConf);
		
		KnowledgeBuilder kbuilder;
		
		kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();

		Iterator<Resource> iterator = loadProcessDefinitions();

		while (iterator.hasNext()) {
			Resource resource = iterator.next();
			kbuilder.add(resource, ResourceType.BPMN2);
		}
		
		
		if (kbuilder.hasErrors()) {
            if (kbuilder.getErrors().size() > 0) {
                boolean errors = false;
                for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                	//logger.warn(error.toString());
                	System.err.println("______________err : " + error.toString());

                    errors = true;
                }
               
            }
        }
		
		return kbuilder;
	}


	private Iterator<Resource> loadProcessDefinitions() {
		List<Resource> list = new ArrayList<Resource>();

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		URL url = classLoader.getResource("com/tnt/gestionproj/jbpm/process");
		File file = null;
		try {
			file = new File(url.toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (file != null && file.isDirectory()) {
			for (String fbpmn : file.list()) {
				list.add(ResourceFactory
						.newClassPathResource("com/tnt/gestionproj/jbpm/process/"
								+ fbpmn));
			}
		}

		return list.iterator();
	}
	
	private Resource getBpmn(String name) {
		List<Resource> list = new ArrayList<Resource>();

		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		URL url = classLoader.getResource("com/tnt/gestionproj/jbpm/process");
		File file = null;
		try {
			file = new File(url.toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		if (file != null && file.isDirectory()) {
			for (String fbpmn : file.list()) {
				if (name.equals(fbpmn)) {
					return ResourceFactory
							.newClassPathResource("com/tnt/gestionproj/jbpm/process/"
									+ fbpmn);

				}
			}
		}

		return null;
	}
	
	
	/*private KnowledgeBase readKnowledgeBase() throws Exception {

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        String url = "http://localhost:8080/drools-guvnor/org.drools.guvnor.Guvnor/package/defaultPackage/SimpleProcess";

        System.out.println("Going: " + url);

 

        UrlResource resource = (UrlResource) ResourceFactory.newUrlResource(url);

        resource.setBasicAuthentication("enabled");

        resource.setUsername("krisv");

        resource.setPassword("krisv");

       

        kbuilder.add(resource, ResourceType.PKG);
        
        kbuilder.add(ResourceFactory.newUrlResource(
        		new java.net.URL("http://<yourwebhost>:8080/webcontextroot/jbpm/Evaluation.bpmn")), 
        		ResourceType.BPMN2);

       

        if(kbuilder.hasErrors()){

            throw new RuntimeException(kbuilder.getErrors().toString());

        }

        KnowledgeBase knowledgeBase = kbuilder.newKnowledgeBase();

        knowledgeBase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        return knowledgeBase;

    }*/




}

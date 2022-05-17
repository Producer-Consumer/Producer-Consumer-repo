package org.producerconsumer;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.json.JSONObject;
import org.producerconsumer.annotations.Alias;
import org.producerconsumer.annotations.Consumer;
import org.producerconsumer.annotations.FilterType;
import org.producerconsumer.annotations.NoConceal;
import org.producerconsumer.annotations.Producer;
import org.producerconsumer.annotations.ResponsePolicy;
import org.producerconsumer.endpoint.DocumentationBuilder;
import org.producerconsumer.endpoint.DocumentationServlet;
import org.producerconsumer.response.ResponseHelper;
import org.producerconsumer.servlet.ServletBuilder;
import org.producerconsumer.util.ClassScanner;
import org.producerconsumer.util.ConstructorInterpretor;
import org.producerconsumer.util.DynamicMethodDispatcher;
import org.producerconsumer.util.FilterException;
import org.producerconsumer.util.FilterHelper;
import org.producerconsumer.util.MethodsInterpretor;

public class ProducerController {

	private static MemoryHeap heap = MemoryHeap.getInstance();
	
	private String cssPath;

	public void exposeFrom(String basePackage) throws ClassNotFoundException {

		heap.allClasses = ClassScanner.getClasses(basePackage);

		classifyServices();
		DocumentationBuilder documentationBuilder ;
		if(this.cssPath==null)
			documentationBuilder = DocumentationBuilder.getInstance();
		else
			documentationBuilder = DocumentationBuilder.getInstance(this.cssPath);
		heap.docBuilder = documentationBuilder;
		extractServlets();

	}
	
	public void exposeFrom(ArrayList<String> basePackages) throws ClassNotFoundException{
		if(basePackages==null)
		{
			System.out.println("[echo]:No base package specified");
			return;
		}
		for(String packageString: basePackages) {
			ArrayList<Class<?>> classes = ClassScanner.getClasses(packageString);
			if(classes!=null)
				heap.allClasses.addAll(classes);
			
			
		}
		
		classifyServices();
		DocumentationBuilder documentationBuilder ;
		if(this.cssPath==null)
			documentationBuilder = DocumentationBuilder.getInstance();
		else
			documentationBuilder = DocumentationBuilder.getInstance(this.cssPath);
		heap.docBuilder = documentationBuilder;
		extractServlets();
		
	}
	
	public void setcssPath(String path) {
		this.cssPath = path;
	}

	private void extractServlets(){
		
		System.out.println("[echo]:Producers:" + heap.producers);
		try {
			FilterHelper.resolveFilters();
		} catch (FilterException e) {
			System.out.println("[echo]:Encountered following error while resolving filters:"+e.getMessage());
			
		}
		ResponseHelper.determineResponsePolicies();
		Tomcat tomcat = new Tomcat();
		Context ctx = tomcat.addContext("/rest", new File(".").getAbsolutePath());
		tomcat.setPort(8089);
		int count = 0;
		for (Class<?> classObj : heap.producers) {
			if (classObj.isAnnotationPresent(NoConceal.class))
				heap.noconceal = true;
			else
				heap.noconceal = false;
			JSONObject constructorPayload = ConstructorInterpretor.getConstructorPayloadFromClass(classObj);
			heap.constructorPayload = constructorPayload;
			System.out.println("[echo]:Payload->" + constructorPayload);
			String restPath = null;
			if (classObj.isAnnotationPresent(Alias.class))
				restPath = classObj.getAnnotation(Alias.class).path();
			Method[] methods = classObj.getDeclaredMethods();
			MethodsInterpretor.classifyMethods(classObj,methods,restPath);
			DynamicMethodDispatcher dispatcher = new DynamicMethodDispatcher();
			heap.polymorphicMethodsGroup = dispatcher.getOrganizedGroups();
		
			ServletBuilder.createServlets(classObj,restPath);
			System.out.println("[echo]:" + heap);
	

		}
		
		heap.docBuilder.closeDocumentation();
		
		for(Map.Entry<Integer, Class<?>> filter : heap.filtersDefinitions.entrySet()) {
			FilterDef filterDef = new FilterDef();
			filterDef.setFilterName(filter.getValue().getSimpleName());
			filterDef.setFilterClass(filter.getValue().getName());
			ctx.addFilterDef(filterDef);
			FilterMap filterMap = new FilterMap();
			filterMap.setFilterName(filter.getValue().getSimpleName());
			String path = "/*";
			if(heap.filterPaths.get(filter.getValue())!=null)
				path = heap.filterPaths.get(filter.getValue());
			System.out.println("[echo]:Adding filter on path:"+path);
			filterMap.addURLPattern(path);
			ctx.addFilterMap(filterMap);
		}
		
		for(Map.Entry<String, HttpServlet> servlet : heap.servlets.entrySet()) {
			Tomcat.addServlet(ctx, count+"", servlet.getValue());
			ctx.addServletMappingDecoded(servlet.getKey()+"/*", count+"");
			count++;
		}
		
		Context docCtx = tomcat.addContext("/documentation", new File(".").getAbsolutePath());
		Tomcat.addServlet(docCtx, "documentation", new DocumentationServlet());
		docCtx.addServletMappingDecoded("/*", "documentation");
		try {
			tomcat.start();
		} catch (LifecycleException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tomcat.getServer().await();
	}

	

	private void classifyServices() {

		for (Class<?> classObj : heap.allClasses) {
			if (classObj.isAnnotationPresent(Producer.class))
				heap.producers.add(classObj);

			if (classObj.isAnnotationPresent(Consumer.class))
				heap.consumers.add(classObj);
			
			if(classObj.isAnnotationPresent(FilterType.class))
				heap.filters.add(classObj);
			
			if(classObj.isAnnotationPresent(ResponsePolicy.class))
				heap.responsePolicyClasses.add(classObj);
			}
	}

	
}

package org.consumeexpose;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.consumeexpose.annotations.Alias;
import org.consumeexpose.annotations.Consumer;
import org.consumeexpose.annotations.FilterType;
import org.consumeexpose.annotations.NoConceal;
import org.consumeexpose.annotations.Producer;
import org.consumeexpose.annotations.ResponsePolicy;
import org.consumeexpose.endpoint.DocumentationBuilder;
import org.consumeexpose.endpoint.DocumentationServlet;
import org.consumeexpose.response.ResponseHelper;
import org.consumeexpose.servlet.ServletBuilder;
import org.consumeexpose.util.ClassScanner;
import org.consumeexpose.util.ConstructorInterpretor;
import org.consumeexpose.util.FilterException;
import org.consumeexpose.util.FilterHelper;
import org.consumeexpose.util.MethodsInterpretor;
import org.json.JSONObject;

public class Bootstrap {

	private static MemoryHeap heap = MemoryHeap.getInstance();
	
	private String cssPath;

	public void run(String basePackage) throws ClassNotFoundException {

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
			String restPath = getRestPathFromClass(classObj.getName());
			if (classObj.isAnnotationPresent(Alias.class))
				restPath = classObj.getAnnotation(Alias.class).path();
			Method[] methods = classObj.getDeclaredMethods();
			MethodsInterpretor.classifyMethods(classObj,methods);
			
			ServletBuilder.createServlets(classObj);
			System.out.println("[echo]:" + heap);
	

		}
		heap.docBuilder.closeDocumentation();
		
		
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

	private String getRestPathFromClass(String className) {
		return className.replace(".", "/");
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

	public void startServer() throws LifecycleException {
		Tomcat tomcat = new Tomcat();
		tomcat.setPort(8089);

		Context ctx = tomcat.addContext("/test", new File(".").getAbsolutePath());

		Tomcat.addServlet(ctx, "Embedded", new HttpServlet() {
			@Override
			protected void service(HttpServletRequest req, HttpServletResponse resp)
					throws ServletException, IOException {
				System.out.println("[echo]:Inside servlet");
				Writer w = resp.getWriter();

				w.write("Embedded Tomcat servlet.\n");
				w.flush();
				w.close();
			}
		});
		
		/*
		 * FilterDef filterDef = new FilterDef();
		 * filterDef.setFilterName(SentinelControl.class.getSimpleName());
		 * filterDef.setFilterClass(SentinelControl.class.getName());
		 * 
		 * ctx.addFilterDef(filterDef); FilterMap filterMap = new FilterMap();
		 * filterMap.setFilterName(SentinelControl.class.getSimpleName());
		 * filterMap.addURLPattern("/*"); ctx.addFilterMap(filterMap);
		 */
		ctx.addServletMappingDecoded("/*", "Embedded");
		

		tomcat.start();
		tomcat.getServer().await();
	}

}

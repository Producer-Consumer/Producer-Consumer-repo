package org.consumeexpose;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.consumeexpose.annotations.Alias;
import org.consumeexpose.annotations.Consumer;
import org.consumeexpose.annotations.NoConceal;
import org.consumeexpose.annotations.Producer;
import org.consumeexpose.util.ClassScanner;
import org.consumeexpose.util.ConstructorInterpretor;
import org.consumeexpose.util.MethodsInterpretor;
import org.json.JSONObject;

public class Bootstrap {
	
	
	private static MemoryHeap heap = MemoryHeap.getInstance();
	
	public void run(String basePackage) throws ClassNotFoundException {
		
		heap.allClasses = ClassScanner.getClasses(basePackage);
		
		classifyServices();
		extractServlets();
		
		
	}
	
	private void extractServlets() {
		System.out.println("[echo]:Producers:"+heap.producers);
		for(Class<?> classObj : heap.producers) {
			if(classObj.isAnnotationPresent(NoConceal.class))
				heap.noconceal=true;
			else
				heap.noconceal=false;
			JSONObject constructorPayload = ConstructorInterpretor.getConstructorPayloadFromClass(classObj);
			System.out.println("[echo]:Payload->"+constructorPayload);
			String restPath = getRestPathFromClass(classObj.getName());
			if(classObj.isAnnotationPresent(Alias.class))
				restPath = classObj.getAnnotation(Alias.class).path();
			Method[] methods = classObj.getDeclaredMethods();
			MethodsInterpretor.classifyMethods(methods);
			System.out.println("[echo]:"+heap);
			
		}
	}
	
	
	
	
	private String getRestPathFromClass(String className) {
		return className.replace(".", "/");
	}
	
	private void classifyServices() {
		
		for(Class<?> classObj : heap.allClasses) {
			if(classObj.isAnnotationPresent(Producer.class))
				heap.producers.add(classObj);
			
			if(classObj.isAnnotationPresent(Consumer.class))
				heap.consumers.add(classObj);
		}
	}
	
	
	
	
	private void startServer() throws LifecycleException {
		 Tomcat tomcat = new Tomcat();
	        tomcat.setPort(8089);

	        Context ctx = tomcat.addContext("/test", new File(".").getAbsolutePath());

	        Tomcat.addServlet(ctx, "Embedded", new HttpServlet() {
	            @Override
	            protected void service(HttpServletRequest req, HttpServletResponse resp) 
	                    throws ServletException, IOException {
	                
	                Writer w = resp.getWriter();
	                
	                w.write("Embedded Tomcat servlet.\n");
	                w.flush();
	                w.close();
	            }
	        });

	        ctx.addServletMappingDecoded("/*", "Embedded");

	        tomcat.start();
	        tomcat.getServer().await();
	}

}

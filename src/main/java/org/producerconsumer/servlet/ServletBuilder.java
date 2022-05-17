package org.producerconsumer.servlet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.producerconsumer.MemoryHeap;
import org.producerconsumer.response.ResponseHelper;
import org.producerconsumer.util.MethodsInterpretor;
import org.producerconsumer.util.PolymorphicMethods;

public class ServletBuilder {

	private static final String PATH_SEPARATOR = "/";

	private static MemoryHeap heap = MemoryHeap.getInstance();
	
	
	
	
	
	public static void createServlets(Class<?> classDef, String classPath) {

		HashMap<String,Integer> responsePolicy = loadAppropriateResponsePolicy(classDef);
		
		
		HashMap<Method,String> httpMethodDefs = heap.preferredHttpMethods;
		
		
		
		if(!heap.exposedMethods.isEmpty()) {
			System.out.println("[echo]:Exposing methods!");
			for(Method method: heap.exposedMethods) {
				//TODO
				
				String restPath = null;
				if(classPath==null)
					restPath = getAliasFor(classDef.getName(),method.getName());
				else
					restPath = getAliasFor(classPath,method.getName());
				
				if(heap.methodPathAlias.get(method)!=null) {
					restPath = heap.methodPathAlias.get(method);
					restPath = getAliasFor(restPath);
				}
				
				HttpServlet servlet = getServlet(classDef,method);
				if(servlet!=null)
					heap.servlets.put(restPath, servlet);
				heap.docBuilder.writeService(heap.documentationCache.get(method).getHTMLString());
			}
		}
		else {
			for(Map.Entry<Method,String> httpMethodDef : httpMethodDefs.entrySet()) {
				
				
				String restPath = null;
				if(classPath==null)
					restPath = getAliasFor(classDef.getName(),httpMethodDef.getKey().getName());
				else
					restPath = getAliasFor(classPath,httpMethodDef.getKey().getName());
				
				if(heap.methodPathAlias.get(httpMethodDef.getKey())!=null) {
					restPath = heap.methodPathAlias.get(httpMethodDef.getKey());
					restPath = getAliasFor(restPath);
				}
				
				HttpServlet servlet = getServlet(classDef,httpMethodDef.getKey());
				if(servlet!=null)
					heap.servlets.put(restPath, servlet);
				heap.docBuilder.writeService(heap.documentationCache.get(httpMethodDef.getKey()).getHTMLString());
			}
			
		}
		
		
	}
	
	private static HttpServlet getServlet(Class<?> classDef,Method method) {
		
		
		String prefferedHttpMethod = heap.preferredHttpMethods.get(method);
		HashMap<String, Integer> responsePolicy = Util.loadAppropriateResponsePolicy(classDef);
		PolymorphicMethods polymorphicMethods = getPolymorphicMethodsFor(method,prefferedHttpMethod);
		if(heap.definedPolymorphicMethods.contains(polymorphicMethods))
			return null;
		else
			heap.definedPolymorphicMethods.add(polymorphicMethods);
		boolean polymorphicMethod = polymorphicMethods.getMethodsCount()>1?true:false;
		
		switch(prefferedHttpMethod) {
		case MethodsInterpretor.GET:
			return GetServlet.createServlet(classDef, polymorphicMethods,responsePolicy,polymorphicMethod);
		case MethodsInterpretor.POST:
			return PostServlet.createServlet(classDef, polymorphicMethods,responsePolicy,polymorphicMethod);
		case MethodsInterpretor.PUT:
			return PutServlet.createServlet(classDef, polymorphicMethods,responsePolicy,polymorphicMethod);
		case MethodsInterpretor.DELETE:
			return DeleteServlet.createServlet(classDef, polymorphicMethods,responsePolicy,polymorphicMethod);
			
		}
		
		return null;
		
	}
	
	public static HashMap<String,Integer> loadAppropriateResponsePolicy(Class<?> classDef){
		HashMap<String,Integer> responsePolicy = heap.responsePolicies.get(classDef.getName());
		if(responsePolicy!=null)
			return responsePolicy;
		responsePolicy = heap.responsePolicies.get(ResponseHelper.ALL);
		
		return responsePolicy;
			
			
	}

	public static String getAliasFor(String className, String methodName) {

		String alias = "";
		className = className.replace(".", "/");
		if (!className.startsWith(PATH_SEPARATOR))
			alias = PATH_SEPARATOR + className;

		if (!className.endsWith(PATH_SEPARATOR))
			alias += PATH_SEPARATOR;
		
		return alias+methodName;
	}
	
	public static String getAliasFor(String methodPathAlias) {
		String alias = "";
		if(!methodPathAlias.startsWith(PATH_SEPARATOR))
			alias = PATH_SEPARATOR+methodPathAlias;
		if(methodPathAlias.endsWith(PATH_SEPARATOR))
			alias = alias.substring(0,alias.length()-1);
		
		return alias;
	}
	
	private static PolymorphicMethods getPolymorphicMethodsFor(Method method,String httpMethod) {
		for(PolymorphicMethods polymorphicMethods: heap.polymorphicMethodsGroup) {
			if(polymorphicMethods.getMethodName().equals(method.getName())&& httpMethod==polymorphicMethods.getHttpMethod())
				return polymorphicMethods;
		}
		return null;
	}

}

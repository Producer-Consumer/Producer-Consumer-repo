package org.consumeexpose.servlet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.consumeexpose.MemoryHeap;
import org.consumeexpose.endpoint.RESTfulService;
import org.consumeexpose.response.ResponseHelper;
import org.consumeexpose.util.ConstructorInterpretor;
import org.consumeexpose.util.MethodsInterpretor;
import org.json.JSONObject;

public class ServletBuilder {

	private static final String PATH_SEPARATOR = "/";

	private static MemoryHeap heap = MemoryHeap.getInstance();
	
	private static JSONObject constructorPayload;
	
	
	
	public static void createServlets(Class<?> classDef) {

		HashMap<String,Integer> responsePolicy = loadAppropriateResponsePolicy(classDef);
		
		boolean responsePolicyExists = (responsePolicy!=null)?true:false;
		
		HashMap<Method,String> httpMethodDefs = heap.preferredHttpMethods;
		
		constructorPayload = ConstructorInterpretor.getConstructorPayloadFromClass(classDef);
		
		if(!heap.exposedMethods.isEmpty()) {
			for(Method method: heap.exposedMethods) {
				//TODO
			}
		}
		else {
			for(Map.Entry<Method,String> httpMethodDef : httpMethodDefs.entrySet()) {
				
				
				String restPath = getAliasFor(classDef.getName(),httpMethodDef.getKey().getName());
				
				if(heap.methodPathAlias.get(httpMethodDef.getKey())!=null) {
					restPath = heap.methodPathAlias.get(httpMethodDef.getKey());
					restPath = getAliasFor(restPath);
				}
				
				heap.service = new RESTfulService(httpMethodDef.getKey().getName(),restPath,httpMethodDef.getValue());
				HttpServlet servlet = getServlet(classDef,httpMethodDef.getKey());
				heap.servlets.put(restPath, servlet);
				heap.docBuilder.writeService(heap.service.getHTMLString());
			}
			
		}
		
		
	}
	
	private static HttpServlet getServlet(Class<?> classDef,Method method) {
		
		
		String prefferedHttpMethod = heap.preferredHttpMethods.get(method);
		HashMap<String, Integer> responsePolicy = Util.loadAppropriateResponsePolicy(classDef);
		
		switch(prefferedHttpMethod) {
		case MethodsInterpretor.GET:
			return GetServlet.createServlet(classDef, method,responsePolicy);
		case MethodsInterpretor.POST:
			return PostServlet.createServlet(classDef, method, responsePolicy);
			
		}
		return null;
		
	}
	
	private static HashMap<String,Integer> loadAppropriateResponsePolicy(Class<?> classDef){
		HashMap<String,Integer> responsePolicy = heap.responsePolicies.get(classDef.getName());
		if(responsePolicy!=null)
			return responsePolicy;
		responsePolicy = heap.responsePolicies.get(ResponseHelper.ALL);
		
		return responsePolicy;
			
			
	}

	private static String getAliasFor(String className, String methodName) {

		String alias = "";
		className = className.replace(".", "/");
		if (!className.startsWith(PATH_SEPARATOR))
			alias = PATH_SEPARATOR + className;

		if (!className.endsWith(PATH_SEPARATOR))
			alias += PATH_SEPARATOR;
		
		return alias+methodName;
	}
	
	private static String getAliasFor(String methodPathAlias) {
		String alias = "";
		if(!methodPathAlias.startsWith(PATH_SEPARATOR))
			alias = PATH_SEPARATOR+methodPathAlias;
		if(methodPathAlias.endsWith(PATH_SEPARATOR))
			alias = alias.substring(0,alias.length()-1);
		
		return alias;
	}

}

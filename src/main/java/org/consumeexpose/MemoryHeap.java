package org.consumeexpose;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;

import org.consumeexpose.endpoint.DocumentationBuilder;
import org.consumeexpose.endpoint.RESTfulService;
import org.json.JSONObject;

public class MemoryHeap {

	private static MemoryHeap INSTANCE;
	
	public ArrayList<Class<?>> allClasses ;
	public ArrayList<Class<?>> consumers = new ArrayList<Class<?>>();
	public ArrayList<Class<?>> producers = new ArrayList<Class<?>>();
	public ArrayList<Class<?>> filters = new ArrayList<Class<?>>();
	public ArrayList<Class<?>> responsePolicyClasses = new ArrayList<Class<?>>();
	
 	public HashMap<String,HttpServlet> servlets = new HashMap<String,HttpServlet>();
 	
 	
	
	public ArrayList<Method> exposedMethods = new ArrayList<Method>();
	public ArrayList<Method> staticMethods = new ArrayList<Method>();
	public HashMap<Method,String> methodPathAlias = new HashMap<Method,String>();
	public HashMap<Method,String> definedHttpMethods = new HashMap<Method,String>();
	public HashMap<Method,String> preferredHttpMethods = new HashMap<Method,String>();
	public HashMap<Integer,Class<?>> filtersDefinitions = new HashMap<Integer,Class<?>>();
	public HashMap<String, HashMap<String,Integer>> responsePolicies = new HashMap<String,HashMap<String,Integer>>();//ClassName vs (Response Signature Vs Response Code)
	
	public boolean noconceal = false;
	
	public RESTfulService service;
	
	public DocumentationBuilder docBuilder;
	
	public JSONObject constructorPayload;
	
	public String requestBody;
	
	public HashMap<String,String> queryParams;
	public HashMap<String,String> responseHeaders;
	public HashMap<String,String> requestHeaders;
	public HashMap<Integer,String> response;
	
	public void clearDocumentationCache() {
		responseHeaders = null;
		requestHeaders = null;
		response = null;
		queryParams = null;
		constructorPayload=null;
		requestBody=null;
	}
	
	public void clearMethodsCache() {
		exposedMethods.clear();
		staticMethods.clear();
		methodPathAlias.clear();
		definedHttpMethods.clear();
		preferredHttpMethods.clear();
	}
	
	
	private MemoryHeap() {
		
	}
	
	public static MemoryHeap getInstance() {
		if(INSTANCE==null) {
			INSTANCE = new MemoryHeap();
			return INSTANCE;
		}
		else
			return INSTANCE;
	}


	@Override
	public String toString() {
		return "MemoryHeap [allClasses=" + allClasses + ", consumers=" + consumers + ", producers=" + producers
				+ ", filters=" + filters + ", responsePolicyClasses=" + responsePolicyClasses + ", servlets=" + servlets
				+ ", exposedMethods=" + exposedMethods + ", staticMethods=" + staticMethods + ", methodPathAlias="
				+ methodPathAlias + ", definedHttpMethods=" + definedHttpMethods + ", preferredHttpMethods="
				+ preferredHttpMethods + ", filtersDefinitions=" + filtersDefinitions + ", responsePolicies="
				+ responsePolicies + ", noconceal=" + noconceal + "]";
	}
	
	
	
}

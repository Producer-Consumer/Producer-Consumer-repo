package org.consumeexpose;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;

public class MemoryHeap {

	private static MemoryHeap INSTANCE;
	
	public ArrayList<Class<?>> allClasses ;
	public ArrayList<Class<?>> consumers = new ArrayList<Class<?>>();
	public ArrayList<Class<?>> producers = new ArrayList<Class<?>>();
	public HashMap<String,HttpServlet> servlets = new HashMap<String,HttpServlet>();
	
	public ArrayList<Method> exposedMethods = new ArrayList<Method>();
	public ArrayList<Method> staticMethods = new ArrayList<Method>();
	public HashMap<String,String> methodPathAlias = new HashMap<String,String>();
	public HashMap<Method,String> definedHttpMethods = new HashMap<Method,String>();
	public HashMap<Method,String> preferredHttpMethods = new HashMap<Method,String>();
	
	public boolean noconceal = false;
	
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
				+ ", servlets=" + servlets + ", exposedMethods=" + exposedMethods + ", staticMethods=" + staticMethods
				+ ", methodPathAlias=" + methodPathAlias + ", definedHttpMethods=" + definedHttpMethods
				+ ", preferredHttpMethods=" + preferredHttpMethods + "]";
	}
	
	
	
}

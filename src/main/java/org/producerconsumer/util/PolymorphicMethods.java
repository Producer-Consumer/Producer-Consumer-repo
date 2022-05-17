package org.producerconsumer.util;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class PolymorphicMethods {

	private ArrayList<Method> methods;
	private String methodName;
	private String httpMethod;
	
	
	public int getMethodsCount() {
		return this.methods.size();
	}
	
	public PolymorphicMethods() {
		this.methods = new ArrayList<Method>();
	}
	public ArrayList<Method> getMethods() {
		return methods;
	}
	public void setMethods(ArrayList<Method> methods) {
		this.methods = methods;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getHttpMethod() {
		return httpMethod;
	}
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	
	public void add(Method method) {
		this.methods.add(method);
	}
	@Override
	public String toString() {
		return "PolymorphicMethods [methods=" + methods + ", methodName=" + methodName + ", httpMethod=" + httpMethod
				+ "]";
	}
	

	
	
	
	
}

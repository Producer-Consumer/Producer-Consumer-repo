package org.consumeexpose.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;

import org.json.JSONObject;

public class ConstructorInterpretor {
	
	public static JSONObject getConstructorPayloadFromClass(Class<?> className) {
		Constructor<?>[] constructors = className.getConstructors();
		if(constructors==null)
			return null;
		Constructor<?> constructor = getSuitableConstructor(constructors);
		Parameter[] params = constructor.getParameters();
		if(params.length==0) {
			return null;
		}
		
		JSONObject constructorPayload = new JSONObject();
		JSONObject constructorInnerPayload = new JSONObject();
		for(Parameter param: params) {
			if(!isPrimitive(param.getType())) {
				constructorInnerPayload.put(param.getName(), getConstructorPayloadFromClass(param.getType()));
			}
			else {
				constructorInnerPayload.put(param.getName(), "");
			}
		}
		
		constructorPayload.put(getNameFromConstructor(constructor.getName()), constructorInnerPayload);
		
		return constructorPayload;
		
	}
	
	public static String getNameFromConstructor(String qualifiedConstructor) {
		int index = qualifiedConstructor.lastIndexOf(".");
		return qualifiedConstructor.substring(index+1);
	}
	
	private static boolean isPrimitive(Class<?> className) {
		return className.isPrimitive()|| className == String.class;
	}
	
	public static Constructor<?> getSuitableConstructor(Constructor<?>[] constructors){
		int paramCount=0, constructorIndex=0;
		Parameter[] params=null;
		int count=0;
		for(Constructor<?> constructor: constructors) {
			params = constructor.getParameters();
			if(paramCount<params.length) {
				paramCount= params.length;
				constructorIndex = count; 
				}
			count++;
		}
		
		return constructors[constructorIndex];
	}

}

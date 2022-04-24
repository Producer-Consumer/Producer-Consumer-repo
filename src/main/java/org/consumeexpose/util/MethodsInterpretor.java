package org.consumeexpose.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.consumeexpose.MemoryHeap;
import org.consumeexpose.annotations.Alias;
import org.consumeexpose.annotations.Delete;
import org.consumeexpose.annotations.Expose;
import org.consumeexpose.annotations.Get;
import org.consumeexpose.annotations.Header;
import org.consumeexpose.annotations.Patch;
import org.consumeexpose.annotations.Post;
import org.consumeexpose.annotations.Put;

public class MethodsInterpretor {

	private static MemoryHeap heap = MemoryHeap.getInstance();
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	public static final String PATCH = "PATCH";
	private static final String PUT_METHOD_REGX = ".*update.*";
	private static final String DELETE_METHOD_REGX = ".*((delete)|(remove)|(purge)|(close)).*";

	public static void classifyMethods(Method[] methods) {

		int modifier;
		heap.clearMethodsCache();
		for (Method method : methods) {
			if (method.isAnnotationPresent(Expose.class))
				heap.exposedMethods.add(method);
			modifier = method.getModifiers();
			if (Modifier.isStatic(modifier))
				heap.staticMethods.add(method);
			if (method.isAnnotationPresent(Alias.class))
				heap.methodPathAlias.put(method.getName(), method.getAnnotation(Alias.class).path());
			String definedHttpMethod = extractDefinedHttpMethod(method);
			if (definedHttpMethod != null)
				heap.definedHttpMethods.put(method, definedHttpMethod);
			heap.preferredHttpMethods.put(method, getPrefferedHttpMethod(method));
		}

	}

	private static String extractDefinedHttpMethod(Method method) {
		if (method.isAnnotationPresent(Get.class))
			return GET;
		else if (method.isAnnotationPresent(Post.class))
			return POST;
		else if (method.isAnnotationPresent(Put.class))
			return PUT;
		else if (method.isAnnotationPresent(Delete.class))
			return DELETE;
		else if (method.isAnnotationPresent(Patch.class))
			return PATCH;
		else
			return null;
	}

	private static String getPrefferedHttpMethod(Method method) {
		
		Parameter[] params = method.getParameters();
	
		params = removeHeaderParamFrom(params);
	
		if (isMethodNameEligibleForPut(method.getName())) {
			return PUT;
		} else if (isMethodNameEligibleForDelete(method.getName())) {
			
			if (areNonPrimitivesPresent(params))
				return POST;
			else
				return DELETE;

		}
		
		if(heap.noconceal&&arePrimitivesOfGet(params)) {
			return GET;
		}
		
		if(params.length==1&&arePrimitivesOfGet(params))
			return GET;
		else 
			return POST;
		

	}
	
	private static Parameter[] removeHeaderParamFrom(Parameter[] params) {
		
		Parameter[] iteratedParams = new Parameter[params.length];
		Parameter[] returnList = null;
		Parameter param=null;
		int count=0;
		boolean headerExists=false;
		for(int paramIterator=0;paramIterator<params.length;paramIterator++) {
			param = params[paramIterator];
			if(param.isAnnotationPresent(Header.class)) {
				headerExists = true;
				continue;
			}
			
			iteratedParams[count]= param;
			count++;
		}
		if(headerExists) {
			returnList = new Parameter[params.length-1];
			for(int paramIterator=0;paramIterator<returnList.length;paramIterator++) {
				param = params[paramIterator];
				
				iteratedParams[paramIterator]= param;
			}
			
			return returnList;
		}
		return params;
	}

	private static boolean isMethodNameEligibleForPut(String methodName) {
		methodName = methodName.toLowerCase();
		Pattern pattern = Pattern.compile(PUT_METHOD_REGX);
		Matcher matcher = pattern.matcher(methodName);
		if (matcher.matches())
			return true;
		else
			return false;
	}

	private static boolean isMethodNameEligibleForDelete(String methodName) {
		methodName = methodName.toLowerCase();
		Pattern pattern = Pattern.compile(DELETE_METHOD_REGX);
		Matcher matcher = pattern.matcher(methodName);
		if (matcher.matches())
			return true;
		else
			return false;
	}

	private static boolean areNonPrimitivesPresent(Parameter[] params) {
		for (Parameter param : params) {
			Class<?> paramClass = param.getType();
			if (!paramClass.isPrimitive() || !(paramClass == String.class)) {
				return false;
			}

		}

		return true;
	}

	private static boolean arePrimitivesOfGet(Parameter[] params) {
		for (Parameter param : params) {
			Class<?> paramClass = param.getType();

			if (paramClass == java.lang.Double.TYPE || paramClass == java.lang.Float.TYPE
					|| paramClass == java.lang.Long.TYPE || paramClass == BigInteger.class|| paramClass == java.lang.Boolean.TYPE)
				return false;
		}

		return true;
	}

}

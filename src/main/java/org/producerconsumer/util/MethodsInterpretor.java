package org.producerconsumer.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.producerconsumer.MemoryHeap;
import org.producerconsumer.annotations.Alias;
import org.producerconsumer.annotations.Delete;
import org.producerconsumer.annotations.Expose;
import org.producerconsumer.annotations.Get;
import org.producerconsumer.annotations.Header;
import org.producerconsumer.annotations.Patch;
import org.producerconsumer.annotations.Placeholder;
import org.producerconsumer.annotations.Post;
import org.producerconsumer.annotations.Put;
import org.producerconsumer.endpoint.RESTfulService;
import org.producerconsumer.response.Response;
import org.producerconsumer.response.ResponseHelper;
import org.producerconsumer.servlet.ServletBuilder;

public class MethodsInterpretor {

	private static MemoryHeap heap = MemoryHeap.getInstance();
	public static final String GET = "GET";
	public static final String POST = "POST";
	public static final String PUT = "PUT";
	public static final String DELETE = "DELETE";
	public static final String PATCH = "PATCH";
	private static final String PUT_METHOD_REGX = ".*update.*";
	private static final String DELETE_METHOD_REGX = ".*((delete)|(remove)|(purge)|(close)).*";

	public static void classifyMethods(Class<?> classDef, Method[] methods) {

		int modifier;
		heap.clearMethodsCache();

		for (Method method : methods) {
			heap.clearDocumentationCache();

			HashMap<String, Integer> responsePolicy = ServletBuilder.loadAppropriateResponsePolicy(classDef);

			if (method.isAnnotationPresent(Expose.class))
				heap.exposedMethods.add(method);
			modifier = method.getModifiers();
			if (Modifier.isStatic(modifier))
				heap.staticMethods.add(method);
			if (method.isAnnotationPresent(Alias.class))
				heap.methodPathAlias.put(method, method.getAnnotation(Alias.class).path());
			String definedHttpMethod = extractDefinedHttpMethod(method);
			if (definedHttpMethod != null)
				heap.definedHttpMethods.put(method, definedHttpMethod);

			String restPath = ServletBuilder.getAliasFor(classDef.getName(), method.getName());

			if (heap.methodPathAlias.get(method) != null) {
				restPath = heap.methodPathAlias.get(method);
				restPath = ServletBuilder.getAliasFor(restPath);
			}

			heap.preferredHttpMethods.put(method, getPrefferedHttpMethod(method));
			heap.service = new RESTfulService(method.getName(), restPath, heap.preferredHttpMethods.get(method));
			Class<?> returnType = method.getReturnType();
			Response[] responses = null;
			if (returnType == java.lang.Void.TYPE)
				responses = ResponseHelper.getPossibleVoidResponses(responsePolicy);
			else if (returnType.isPrimitive() || returnType == String.class) {
				responses = ResponseHelper.getPossiblePrimitiveResponses(returnType, responsePolicy);
			} else {
				responses = ResponseHelper.getPossibleNonPrimitiveResponses(returnType, responsePolicy);
			}

			HashMap<Integer, String> responsesMap = responseToHashMap(responses);
			heap.response = responsesMap;

			setRESTfulValues();
			System.out.println("[echo]:Writing service:" + method.getName());
			heap.documentationCache.put(method, heap.service);

		}

	}

	private static HashMap<Integer, String> responseToHashMap(Response[] responses) {
		if (responses == null || responses.length < 1)
			return null;
		else {
			HashMap<Integer, String> responseMap = new HashMap<Integer, String>();
			for (Response response : responses) {
				responseMap.put(response.getStatusCode(), response.getResponseBody());
			}
			return responseMap;
		}
	}

	private static void setRESTfulValues() {
		if (heap.requestBody != null)
			heap.service.setBody(heap.requestBody);
		if (heap.queryParams != null) {

			heap.service.setQueryParams(heap.queryParams);
		}
		if (heap.requestHeaders != null)
			heap.service.setRequestHeaders(heap.requestHeaders);
		if (heap.responseHeaders != null)
			heap.service.setResponseHeaders(heap.responseHeaders);
		if (heap.response != null)
			heap.service.setResponse(heap.response);
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

		params = removeHTTPDefinitions(params);

		if (isMethodNameEligibleForPut(method.getName())) {
			return putProcessor(params);
		} else if (isMethodNameEligibleForDelete(method.getName())) {

			if (areNonPrimitivesPresent(params))
				return postProcessor(params);
			else
				return deleteProcessor(params);

		}

		if (((heap.noconceal || params.length < 2) && arePrimitivesOfGet(params))) {

			return getProcessor(params);
		}

		if (params.length == 1 && arePrimitivesOfGet(params))
			return getProcessor(params);
		else
			return postProcessor(params);

	}

	private static String getProcessor(Parameter[] params) {

		if (params.length > 0)
			heap.queryParams = new HashMap<String, String>();
		for (Parameter param : params) {
			heap.queryParams.put(param.getName(), getPlaceholder(param));

		}

		return GET;
	}

	private static String deleteProcessor(Parameter[] params) {

		if (params.length > 0)
			heap.queryParams = new HashMap<String, String>();
		for (Parameter param : params) {
			heap.queryParams.put(param.getName(), getPlaceholder(param));

		}

		return DELETE;
	}

	private static String postProcessor(Parameter[] params) {

		JSONObject postPayload = new JSONObject();
		if (heap.constructorPayload != null) {
			Iterator<String> keyIterator = heap.constructorPayload.keys();
			String key = keyIterator.next();
			if (key != null) {
				postPayload.put(key, heap.constructorPayload.getJSONObject(key));
			}
		}
		for (Parameter param : params) {
			if (isPrimitive(param)) {
				postPayload.put(param.getName(), getPlaceholder(param));
			} else {
				JSONObject paramJson = ConstructorInterpretor.getConstructorPayloadFromClass(param.getType());
				if (paramJson != null)
					postPayload.put(param.getName(), paramJson.getJSONObject(param.getType().getSimpleName()));
				else {
					System.out
							.println("[echo]:Warning:Expecting a valid constructor for param type:" + param.getType());
				}
			}

		}

		heap.requestBody = postPayload.toString(1);

		return POST;
	}

	private static String putProcessor(Parameter[] params) {

		JSONObject postPayload = new JSONObject();
		if (heap.constructorPayload != null) {
			Iterator<String> keyIterator = heap.constructorPayload.keys();
			String key = keyIterator.next();
			if (key != null) {
				postPayload.put(key, heap.constructorPayload.getJSONObject(key));
			}
		}
		for (Parameter param : params) {
			if (isPrimitive(param)) {
				postPayload.put(param.getName(), getPlaceholder(param));
			} else {
				JSONObject paramJson = ConstructorInterpretor.getConstructorPayloadFromClass(param.getType());
				if (paramJson != null)
					postPayload.put(param.getName(), paramJson.getJSONObject(param.getType().getSimpleName()));
				else {
					System.out
							.println("[echo]:Warning:Expecting a valid constructor for param type:" + param.getType());
				}
			}

		}

		heap.requestBody = postPayload.toString(1);

		return PUT;
	}

	public static Parameter[] removeHTTPDefinitions(Parameter[] params) {

		Parameter[] iteratedParams = new Parameter[params.length];
		Parameter[] returnList = null;
		Parameter param = null;
		int count = 0;

		int definitionsCount = 0;
		for (int paramIterator = 0; paramIterator < params.length; paramIterator++) {
			param = params[paramIterator];
			if (isHttpDefinition(param)) {
				definitionsCount++;
				continue;
			}

			iteratedParams[count] = param;
			count++;
		}

		if (definitionsCount > 0) {

			returnList = new Parameter[params.length - definitionsCount];
			for (int paramIterator = 0; paramIterator < count; paramIterator++) {
				param = iteratedParams[paramIterator];
				returnList[paramIterator] = param;
			}

			return returnList;
		}
		return params;
	}

	private static boolean isHttpDefinition(Parameter param) {

		if (param.isAnnotationPresent(Header.class))
			return true;
		else if (param.getType() == HttpServletRequest.class)
			return true;
		else if (param.getType() == HttpServletResponse.class)
			return true;
		else
			return false;

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

		boolean result = false;

		for (Parameter param : params) {
			Class<?> paramClass = param.getType();
			if (!paramClass.isPrimitive() || !(paramClass == String.class)) {
				return false;
			}

		}

		return result;
	}

	private static boolean arePrimitivesOfGet(Parameter[] params) {

		boolean marker = true;
		for (Parameter param : params) {
			Class<?> paramClass = param.getType();

			if (paramClass != java.lang.Integer.TYPE && paramClass != java.lang.Boolean.TYPE
					&& paramClass != java.lang.Short.TYPE && paramClass != java.lang.Character.TYPE)
				marker = false;
			/*
			 * if (paramClass == java.lang.Double.TYPE || paramClass == java.lang.Float.TYPE
			 * || paramClass == java.lang.Long.TYPE || paramClass == BigInteger.class||
			 * paramClass == java.lang.Boolean.TYPE) return false;
			 */
		}

		return marker;
	}

	public static JSONObject getParamPayload(Parameter[] params) {

		JSONObject paramPayload = new JSONObject();
		JSONObject constructorPayload = null;
		for (Parameter param : params) {
			if (isPrimitive(param))
				paramPayload.put(param.getName(), getPlaceholder(param));
			else {
				constructorPayload = ConstructorInterpretor.getConstructorPayloadFromClass(param.getType());
				Iterator<String> keyIterator = constructorPayload.keys();
				paramPayload.put(param.getName(), constructorPayload.get(keyIterator.next()));
			}
		}

		return paramPayload;

	}

	public static boolean isPrimitive(Parameter param) {
		if (param.getType().isPrimitive() || param.getType() == String.class)
			return true;
		else
			return false;
	}

	private static String getPlaceholder(Parameter param) {

		String value = "";
		if (param.isAnnotationPresent(Placeholder.class))
			value = param.getAnnotation(Placeholder.class).value();

		return value;
	}

}

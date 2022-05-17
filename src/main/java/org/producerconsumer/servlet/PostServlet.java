package org.producerconsumer.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.producerconsumer.annotations.Header;
import org.producerconsumer.response.Response;
import org.producerconsumer.response.ResponseHelper;
import org.producerconsumer.util.DeserializationException;
import org.producerconsumer.util.DeserializationHelper;
import org.producerconsumer.util.DynamicMethodDispatcher;
import org.producerconsumer.util.MethodsInterpretor;
import org.producerconsumer.util.PolymorphicMethods;

public class PostServlet {

	public static final String METHOD_TYPE = MethodsInterpretor.POST;

	public static HttpServlet createServlet(Class<?> classDef, PolymorphicMethods polymorphicMethods, HashMap<String, Integer> responsePolicy,boolean polymorphic) {
		
		if(polymorphic)
			return getPolymorphicServlet(classDef,polymorphicMethods,responsePolicy);
		else
			return getNonPolymorphicServlet(classDef,polymorphicMethods,responsePolicy);
		
	}
	
	private static HttpServlet getNonPolymorphicServlet(Class<?> classDef, PolymorphicMethods polymorphicMethods, HashMap<String, Integer> responsePolicy) {
		
		Method method = polymorphicMethods.getMethods().get(0);
		
		Parameter[] params = method.getParameters();
		params = MethodsInterpretor.removeHTTPDefinitions(params);

		HttpServlet postServlet = new HttpServlet() {

			
			private static final long serialVersionUID = 1L;

			@Override
			protected void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
			
				String requestBody = readRequestBody(request);
				HashMap<String, String> headers = Util.getHttpHeader(request);
				try {
					JSONObject payload = getJsonBody(requestBody);
					JSONObject constructorPayload = null;

					try {
						constructorPayload = payload.getJSONObject(classDef.getSimpleName());
					} catch (JSONException e) {
						System.out
								.println("[echo]:Constructor Payload not found for class:" + classDef.getSimpleName());
						constructorPayload = new JSONObject();
					}

					Object targetObject = DeserializationHelper.getInstantiedObjectOf(classDef, constructorPayload);
					Object[] methodArgs = constructMethodArgs(request, response, headers, method.getParameters(),
							payload);

					Object returnValue = method.invoke(targetObject, methodArgs);

					Class<?> returnType = method.getReturnType();
					Response formedResponse = ResponseHelper.getResponseFor(returnType, returnValue, METHOD_TYPE,
							responsePolicy);

					
					if (returnType != java.lang.Void.TYPE) {
						response.setStatus(formedResponse.getStatusCode());
						response.setContentType("application/json");
						response.setContentLength(formedResponse.getResponseBody().length());
						response.getOutputStream().write(formedResponse.getResponseBody().getBytes());

					}
					else {
						response.setStatus(formedResponse.getStatusCode());
					}
					
				} catch (NullPointerException e) {

					response.setStatus(400);
					response.getOutputStream().write(ResponseHelper.getClientError().getBytes());
				} catch (JSONException e) {
					response.setStatus(400);
					response.getOutputStream().write(ResponseHelper.getJSONError().getBytes());
				} catch (Exception e) {
					Throwable cause = e.getCause();
					if(cause!=null) {
					Response responseObject = ResponseHelper.getExceptionResponse(e.getCause().getMessage());
					if (responseObject == null) {
						response.setStatus(500);
						response.getOutputStream().write(ResponseHelper.getInternalServerError().getBytes());
					} else {
						response.setStatus(responseObject.getStatusCode());
						response.getOutputStream().write(responseObject.getResponseBody().getBytes());
					}
					}
					else
					{
						response.setStatus(500);
						response.getOutputStream().write(ResponseHelper.getInternalServerError().getBytes());
					}
				}

			}

		};

		return postServlet;
		
	}
	
	private static HttpServlet getPolymorphicServlet(Class<?> classDef, PolymorphicMethods polymorphicMethods, HashMap<String, Integer> responsePolicy) {
		
		
		HttpServlet postServlet = new HttpServlet() {

			
			private static final long serialVersionUID = 1L;

			@Override
			protected void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {
				Map<String,String[]> headerMap = request.getParameterMap();
				String requestBody = readRequestBody(request);
				int requestParamCount = DynamicMethodDispatcher.getPossibleParameterCountFromRequest(headerMap,requestBody);
				
				HashMap<String, String> headers = Util.getHttpHeader(request);
				try {
					JSONObject payload = getJsonBody(requestBody);
					JSONObject constructorPayload = null;

					try {
						constructorPayload = payload.getJSONObject(classDef.getSimpleName());
						System.out.println("[echo]:Payload contains constructor data");
						requestParamCount--;
					} catch (JSONException e) {
						System.out
								.println("[echo]:Constructor Payload not found for class:" + classDef.getSimpleName());
						constructorPayload = new JSONObject();
					}
					
					Method method = DynamicMethodDispatcher.getSuitableMethodFrom(polymorphicMethods, requestParamCount);
					
					Object targetObject = DeserializationHelper.getInstantiedObjectOf(classDef, constructorPayload);
					Object[] methodArgs = constructMethodArgs(request, response, headers, method.getParameters(),
							payload);

					Object returnValue = method.invoke(targetObject, methodArgs);

					Class<?> returnType = method.getReturnType();
					Response formedResponse = ResponseHelper.getResponseFor(returnType, returnValue, METHOD_TYPE,
							responsePolicy);

					
					if (returnType != java.lang.Void.TYPE) {
						response.setStatus(formedResponse.getStatusCode());
						response.setContentType("application/json");
						response.setContentLength(formedResponse.getResponseBody().length());
						response.getOutputStream().write(formedResponse.getResponseBody().getBytes());

					}
					else {
						response.setStatus(formedResponse.getStatusCode());
					}
					
				} catch (NullPointerException e) {

					response.setStatus(400);
					response.getOutputStream().write(ResponseHelper.getClientError().getBytes());
				} catch (JSONException e) {
					
					response.setStatus(400);
					response.getOutputStream().write(ResponseHelper.getJSONError().getBytes());
					e.printStackTrace();
				} catch (Exception e) {
					Throwable cause = e.getCause();
					if(cause!=null) {
					Response responseObject = ResponseHelper.getExceptionResponse(e.getCause().getMessage());
					if (responseObject == null) {
						response.setStatus(500);
						response.getOutputStream().write(ResponseHelper.getInternalServerError().getBytes());
					} else {
						response.setStatus(responseObject.getStatusCode());
						response.getOutputStream().write(responseObject.getResponseBody().getBytes());
					}
					}
					else
					{
						response.setStatus(500);
						response.getOutputStream().write(ResponseHelper.getInternalServerError().getBytes());
					}
				}

			}

		};

		return postServlet;
		
	}
	
	
	private static Object[] constructMethodArgs(HttpServletRequest request, HttpServletResponse response,
			HashMap<String, String> headers, Parameter[] parameters, JSONObject payload)
			throws JSONException, InstantiationException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, DeserializationException {

		Object[] methodArgs = new Object[parameters.length];
		Parameter param = null;

		for (int paramIterator = 0; paramIterator < parameters.length; paramIterator++) {
			param = parameters[paramIterator];
			if (param.getType() == HttpServletRequest.class) {
				methodArgs[paramIterator] = request;
			} else if (param.getType() == HttpServletResponse.class) {
				methodArgs[paramIterator] = response;
			} else if (param.isAnnotationPresent(Header.class)) {
				if (param.getType() != HashMap.class) {
					System.out.println("[echo]:Can't convert Headers into specified type:" + param.getType());
					methodArgs[paramIterator] = null;
				} else {
					methodArgs[paramIterator] = headers;
				}
			} else {

				if (MethodsInterpretor.isPrimitive(param)) {

					methodArgs[paramIterator] = DeserializationHelper.getValueFrom(param.getType(),
							payload.getString(param.getName()));
				} else {

					methodArgs[paramIterator] = DeserializationHelper.getInstantiedObjectOf(param.getType(),
							payload.getJSONObject(param.getName()));
				}

				// TODO null checks
			}

		}

		return methodArgs;
	}

	private static JSONObject getJsonBody(String requestString) throws JSONException {


		JSONObject jsonPayload = null;
		if (requestString != null&&requestString.length()>2)
			jsonPayload = new JSONObject(requestString);

		return jsonPayload;

	}

	private static String readRequestBody(HttpServletRequest request) {

		String payload = null;
		try {
			byte[] payloadBytes = request.getInputStream().readAllBytes();

			payload = new String(payloadBytes);
		} catch (IOException e) {
			System.out.println("[echo]:Can't read any data from request body");
		}

		return payload;

	}

}

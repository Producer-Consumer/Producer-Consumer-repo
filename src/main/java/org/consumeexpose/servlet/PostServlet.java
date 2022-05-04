package org.consumeexpose.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.consumeexpose.annotations.Header;
import org.consumeexpose.response.Response;
import org.consumeexpose.response.ResponseHelper;
import org.consumeexpose.util.DeserializationException;
import org.consumeexpose.util.DeserializationHelper;
import org.consumeexpose.util.MethodsInterpretor;
import org.json.JSONException;
import org.json.JSONObject;

public class PostServlet {

	public static HttpServlet createServlet(Class<?> classDef, Method method, HashMap<String, Integer> responsePolicy) {

		Parameter[] params = method.getParameters();
		params = MethodsInterpretor.removeHTTPDefinitions(params);

		HttpServlet postServlet = new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void doPost(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				HashMap<String, String> headers = Util.getHttpHeader(request);
				try {
					JSONObject payload = getJsonBody(request);
					JSONObject constructorPayload = null;
					
					try {
						constructorPayload = payload.getJSONObject(classDef.getSimpleName());
					} catch (JSONException e) {
						System.out.println("[echo]:Constructor Payload not found for class:"+classDef.getSimpleName());
						constructorPayload = new JSONObject();
					}
					
					Object targetObject = DeserializationHelper.getInstantiedObjectOf(classDef, constructorPayload);
					Object[] methodArgs = constructMethodArgs(request, response, headers, method.getParameters(),payload);

					Object returnValue = method.invoke(targetObject, methodArgs);

					Class<?> returnType = method.getReturnType();

					if (returnType == Response.class && returnValue != null) {
						Response responseObject = (Response) returnValue;
						response.setStatus(responseObject.getStatusCode());
						response.setContentType("application/json");
						response.setContentLength(returnValue.toString().length());
						response.getOutputStream().write(responseObject.getResponseBody().getBytes());
					} else {

						int responseCode = Util.getAppropriateResponseCode(returnValue, responsePolicy,
								method.getReturnType(), MethodsInterpretor.POST);

						if (returnValue != null) {
							response.setStatus(responseCode);
							response.setContentType("application/json");
							response.setContentLength(returnValue.toString().length());
							response.getOutputStream().write(returnValue.toString().getBytes());
						} else {
							response.setStatus(responseCode);
						}

					}

				} catch (NullPointerException e) {
					
					response.setStatus(400);
					response.getOutputStream().write("Something went wrong from your side!".getBytes());
				} catch (JSONException e) {
					response.setStatus(400);
					response.getOutputStream().write("{\"message\":\"Semantic error\"}".getBytes());
				} catch (Exception e) {

					Response responseObject = ResponseHelper.getExceptionResponse(e.getCause().getMessage());
					if (responseObject == null) {
						response.setStatus(500);
						response.getOutputStream().write("Something went wrong!".getBytes());
					} else {
						response.setStatus(responseObject.getStatusCode());
						response.getOutputStream().write(responseObject.getResponseBody().getBytes());
					}
				}

			}

		};

		return postServlet;
	}

	private static Object[] constructMethodArgs(HttpServletRequest request, HttpServletResponse response,
			HashMap<String, String> headers, Parameter[] parameters,JSONObject payload) throws JSONException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, DeserializationException{

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
				
				if(MethodsInterpretor.isPrimitive(param)) {
					
					methodArgs[paramIterator] = DeserializationHelper.getValueFrom(param.getType(), payload.getString(param.getName()));
				}
				else
					methodArgs[paramIterator] = DeserializationHelper.getInstantiedObjectOf(param.getType(), payload.getJSONObject(param.getName()));
				
				//TODO null checks
			}

		}

		return methodArgs;
	}

	private static JSONObject getJsonBody(HttpServletRequest request) throws JSONException {

		String payload = readRequestBody(request);
		JSONObject jsonPayload = null;
		if (payload != null)
			jsonPayload = new JSONObject(payload);

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

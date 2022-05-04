package org.consumeexpose.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.consumeexpose.annotations.Header;
import org.consumeexpose.response.Response;
import org.consumeexpose.response.ResponseHelper;
import org.consumeexpose.util.DeserializationHelper;
import org.consumeexpose.util.MethodsInterpretor;
import org.json.JSONObject;

public class GetServlet {

	public static HttpServlet createServlet(Class<?> classDef, Method method,HashMap<String, Integer> responsePolicy) {

		Parameter[] params = method.getParameters();
		params = MethodsInterpretor.removeHTTPDefinitions(params);

		HttpServlet getServlet = new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void doGet(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				HashMap<String, String> headers = Util.getHttpHeader(request);
				try {
					Object targetObject = DeserializationHelper.getInstantiedObjectOf(classDef, new JSONObject());
					Object[] methodArgs = constructMethodArgs(request, response, headers, method.getParameters());
					
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
								method.getReturnType(), MethodsInterpretor.GET);

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
					e.printStackTrace();
					response.setStatus(400);
					response.getOutputStream().write("Something went wrong from your side!".getBytes());
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

		return getServlet;
	}

	private static Object[] constructMethodArgs(HttpServletRequest request, HttpServletResponse response,
			HashMap<String, String> headers, Parameter[] parameters) {

		Object[] methodArgs = new Object[parameters.length];
		Parameter param = null;
		Map<String, String[]> queryParamValues = request.getParameterMap();
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
				String[] queryValues = queryParamValues.get(param.getName());
				if (queryValues != null) {
					String value = queryValues[0];// TODO support for Collections and Array
					if (value == null) {
						System.out.println("[echo]:The Specified parameter:" + param.getName()
								+ " is not found in the request Query Params");
						methodArgs[paramIterator] = null;
					} else
						methodArgs[paramIterator] = DeserializationHelper.getValueFrom(param.getType(), value);
				}
			}

		}

		return methodArgs;
	}

}

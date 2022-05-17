package org.producerconsumer.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.producerconsumer.annotations.Header;
import org.producerconsumer.response.Response;
import org.producerconsumer.response.ResponseHelper;
import org.producerconsumer.util.DeserializationHelper;
import org.producerconsumer.util.MethodsInterpretor;

public class DeleteServlet {
	
	public static final String METHOD_TYPE = MethodsInterpretor.DELETE;

	public static HttpServlet createServlet(Class<?> classDef, Method method,HashMap<String, Integer> responsePolicy) {

		Parameter[] params = method.getParameters();
		params = MethodsInterpretor.removeHTTPDefinitions(params);

		HttpServlet deleteServlet = new HttpServlet() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void doDelete(HttpServletRequest request, HttpServletResponse response)
					throws ServletException, IOException {

				HashMap<String, String> headers = Util.getHttpHeader(request);
				try {
					Object targetObject = DeserializationHelper.getInstantiedObjectOf(classDef, new JSONObject());
					Object[] methodArgs = constructMethodArgs(request, response, headers, method.getParameters());
					
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
					response.setContentType("application/json");
					response.getOutputStream().write(ResponseHelper.getClientError().getBytes());
				} catch (ArguementsException e) {
					response.setStatus(400);
					response.setContentType("application/json");
					response.getOutputStream().write(ResponseHelper.getClientError().getBytes());
				} catch (Exception e) {
					Throwable cause = e.getCause();
					if(cause!=null) {
					Response responseObject = ResponseHelper.getExceptionResponse(e.getCause().getMessage());
					if (responseObject == null) {
						response.setStatus(500);
						response.setContentType("application/json");
						response.getOutputStream().write(ResponseHelper.getInternalServerError().getBytes());
					} else {
						response.setStatus(responseObject.getStatusCode());
						response.setContentType("application/json");
						response.getOutputStream().write(responseObject.getResponseBody().getBytes());
					}
					}
					else
					{
						response.setStatus(500);
						response.setContentType("application/json");
						response.getOutputStream().write(ResponseHelper.getInternalServerError().getBytes());
					}
				}

			}

		};

		return deleteServlet;
	}

	private static Object[] constructMethodArgs(HttpServletRequest request, HttpServletResponse response,
			HashMap<String, String> headers, Parameter[] parameters) throws ArguementsException{

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
				else
					throw new ArguementsException("Required Parameters are not found in query params");
			}

		}

		return methodArgs;
	}


}

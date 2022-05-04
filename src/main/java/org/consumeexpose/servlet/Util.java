package org.consumeexpose.servlet;

import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.consumeexpose.MemoryHeap;
import org.consumeexpose.response.ResponseHelper;
import org.consumeexpose.util.MethodsInterpretor;

public class Util {

	private static MemoryHeap heap = MemoryHeap.getInstance();

	public static HashMap<String, String> getHttpHeader(HttpServletRequest request) {
		HashMap<String, String> headers = new HashMap<String, String>();

		Iterator<String> headerIterator = request.getHeaderNames().asIterator();

		while (headerIterator.hasNext()) {
			String key = headerIterator.next();
			headers.put(key, request.getHeader(key));

		}

		return headers;
	}

	public static HashMap<String, Integer> loadAppropriateResponsePolicy(Class<?> classDef) {
		HashMap<String, Integer> responsePolicy = heap.responsePolicies.get(classDef.getName());
		if (responsePolicy != null)
			return responsePolicy;
		responsePolicy = heap.responsePolicies.get(ResponseHelper.ALL);

		return responsePolicy;

	}

	public static int getAppropriateResponseCode(Object returnValue, HashMap<String, Integer> responsePolicy,
			Class<?> returnType, String methodType) {

		boolean policyExists = (responsePolicy == null) ? false : true;

		if (returnValue == null) {
			if (returnType == Void.TYPE) {
				if (policyExists) {
					if (responsePolicy.get(ResponseHelper.VOID) != null)
						return responsePolicy.get(ResponseHelper.VOID);

				}
				return handleVoidTypeResponse(methodType);

			} else {
				if (policyExists) {
					if (responsePolicy.get(ResponseHelper.NULL) != null)
						return responsePolicy.get(ResponseHelper.NULL);

				}

				return ResponseHelper.NULL_RESPONSE_CODE;

			}

		} else if (policyExists) {

			if (responsePolicy.get(returnValue.getClass().getName()) != null)
				return responsePolicy.get(returnValue.getClass().getName());
			else
				return ResponseHelper.SUCCESS;
		} else
			return ResponseHelper.SUCCESS;

	}

	private static int handleVoidTypeResponse(String methodType) {

		if (methodType == MethodsInterpretor.GET)
			return ResponseHelper.VOID_RESPONSE_CODE_GET;
		else if (methodType == MethodsInterpretor.POST || methodType == MethodsInterpretor.PUT)
			return ResponseHelper.VOID_RESPONSE_CODE_POST;
		else if (methodType == MethodsInterpretor.DELETE)
			return ResponseHelper.VOID_RESPONSE_CODE_DELETE;
		else
			return ResponseHelper.SUCCESS;

	}
}

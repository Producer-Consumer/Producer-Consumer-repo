package org.producerconsumer.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.producerconsumer.MemoryHeap;

public class DynamicMethodDispatcher {

	private MemoryHeap heap;

	public DynamicMethodDispatcher() {
		this.heap = MemoryHeap.getInstance();
	}

	public PolymorphicMethodsGroup getOrganizedGroups() {

		PolymorphicMethodsGroup organizedGroups = null;
		HashMap<String, HashMap<String, PolymorphicMethods>> dictionary = new HashMap<String, HashMap<String, PolymorphicMethods>>();

		HashMap<Method, String> httpMethodDefs = heap.preferredHttpMethods;
		PolymorphicMethods polymorphicMethods = null;
		HashMap<String, PolymorphicMethods> innerMap = null;
		for (Map.Entry<Method, String> httpMethodDef : httpMethodDefs.entrySet()) {

			if (dictionary.get(httpMethodDef.getKey().getName()) != null) {
				innerMap = dictionary.get(httpMethodDef.getKey().getName());
				if (innerMap.get(httpMethodDef.getValue()) != null) {
					polymorphicMethods = innerMap.get(httpMethodDef.getValue());
					polymorphicMethods.add(httpMethodDef.getKey());
				} else {
					polymorphicMethods = new PolymorphicMethods();
					polymorphicMethods.setMethodName(httpMethodDef.getKey().getName());
					polymorphicMethods.setHttpMethod(httpMethodDef.getValue());
					polymorphicMethods.add(httpMethodDef.getKey());
					innerMap.put(httpMethodDef.getValue(), polymorphicMethods);
				}

			} else {
				polymorphicMethods = new PolymorphicMethods();
				polymorphicMethods.setMethodName(httpMethodDef.getKey().getName());
				polymorphicMethods.setHttpMethod(httpMethodDef.getValue());
				polymorphicMethods.add(httpMethodDef.getKey());
				innerMap = new HashMap<String, PolymorphicMethods>();
				innerMap.put(httpMethodDef.getValue(), polymorphicMethods);
				dictionary.put(httpMethodDef.getKey().getName(), innerMap);
			}

		}

		if (!dictionary.isEmpty()) {
			organizedGroups = new PolymorphicMethodsGroup();
			for (Map.Entry<String, HashMap<String, PolymorphicMethods>> dictionaryEntry : dictionary.entrySet()) {
				innerMap = dictionaryEntry.getValue();
				for (Map.Entry<String, PolymorphicMethods> innerMapEntry : innerMap.entrySet()) {
					organizedGroups.add(innerMapEntry.getValue());
				}
			}
		}

		return organizedGroups;

	}

	public static Method getSuitableMethodFrom(PolymorphicMethods polymorphicMethods, int paramCount) {

		for (Method method : polymorphicMethods.getMethods()) {
			Parameter[] params = method.getParameters();
			params = MethodsInterpretor.removeHTTPDefinitions(params);
			if (params.length == paramCount)
				return method;
		}

		return null;

	}

	public static int getPossibleParameterCountFromRequest(Map<String, String[]> requestParams, String payload) {
		int count = 0;

		if (requestParams != null && !requestParams.isEmpty()) {
			count += requestParams.size();
		}
		if (payload!=null&&payload.length() > 2) {
			try {
				JSONObject requestPayload = new JSONObject(payload);
				if (requestPayload != null) {
					count += requestPayload.keySet().size();
				}
			} catch (JSONException e) {

			}
		}

		return count;
	}

}

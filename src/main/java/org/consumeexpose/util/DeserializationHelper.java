package org.consumeexpose.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;

import org.json.JSONException;
import org.json.JSONObject;

public class DeserializationHelper {

	public static Object getInstantiedObjectOf(Class<?> classDefinition, JSONObject constructorPayload) throws DeserializationException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Constructor<?> constructor = ConstructorInterpretor.getSuitableConstructor(classDefinition.getConstructors());
		Parameter[] params = constructor.getParameters();
		Object[] constructorValues = new Object[params.length];
		Parameter param = null;
		Class<?> classType = null;
		String paramValue = null;
		for (int paramIterator = 0; paramIterator < params.length; paramIterator++) {
			
			param = params[paramIterator];
			
			classType = param.getType();
			
			if(classType.isPrimitive()||classType==String.class) {
				try {
					paramValue = constructorPayload.getString(param.getName());
				} catch (JSONException e) {
					throw new DeserializationException("Payload Integrity failed, the following key is not found:"+param.getName());
				}
				constructorValues[paramIterator] = getValueFrom(classType,paramValue);
			}
			else
				constructorValues[paramIterator] = getInstantiedObjectOf(classType,constructorPayload.getJSONObject(param.getName()));
		}
		
		
		
		return constructor.newInstance(constructorValues);
	}

	private static Object getValueFrom(Class<?> classType, String value) {
		if (classType == java.lang.Short.TYPE)
			return Short.parseShort(value);
		else if (classType == String.class)
			return value;
		else if (classType== java.lang.Integer.TYPE)
			return Integer.parseInt(value);
		else if (classType == java.lang.Double.TYPE)
			return Double.parseDouble(value);
		else if (classType == java.lang.Float.TYPE)
			return Float.parseFloat(value);
		else if (classType == java.lang.Long.TYPE)
			return Long.parseLong(value);
		else if (classType == java.lang.Character.TYPE)
			return value;
		else if (classType == java.lang.Byte.TYPE)
			return Byte.parseByte(value);
		else if (classType == java.lang.Boolean.TYPE)
			return Boolean.parseBoolean(value);
		else
			return null;

	}

}

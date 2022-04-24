package org.consumeexpose;

import java.lang.reflect.InvocationTargetException;

import org.consumeexpose.util.DeserializationException;
import org.consumeexpose.util.DeserializationHelper;
import org.json.JSONObject;

public class Test {
	
	public static void main(String args[]) {
		
		
		Bootstrap bootstrapObj = new Bootstrap();
		try {
			//bootstrapObj.run("org.consumeexpose");
			//System.out.println("[echo]:Classes:"+ClassScanner.getClasses("org.consumeexpose"));
			JSONObject payload = new JSONObject();
			payload.put("number", "123");
			payload.put("identifier", "Batman");
			JSONObject innerPayload = new JSONObject();
			innerPayload.put("colorNumber", "456");
			innerPayload.put("identifier", "Brown");
			payload.put("colorWay", innerPayload);
			System.out.println("[echo]:Payload:"+payload);
			Product product = (Product) DeserializationHelper.getInstantiedObjectOf(Product.class, payload);
			System.out.println("[echo]:"+product);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | DeserializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

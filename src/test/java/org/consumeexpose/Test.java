package org.consumeexpose;

import org.json.JSONObject;

public class Test {
	
	public static void main(String args[]) {
		
		
		Bootstrap bootstrapObj = new Bootstrap();
		String cssPath = "/home/naveen/eclipse-workspace/Consumer-Producer/style.css";
		bootstrapObj.setcssPath(cssPath);
		try {
			bootstrapObj.run("org.consumeexpose");
			//bootstrapObj.startServer();
			//System.out.println("[echo]:Classes:"+ClassScanner.getClasses("org.consumeexpose"));
			JSONObject payload = new JSONObject();
			payload.put("number", "123");
			payload.put("identifier", "Batman");
			JSONObject innerPayload = new JSONObject();
			innerPayload.put("colorNumber", "456");
			innerPayload.put("identifier", "Brown");
			payload.put("colorWay", innerPayload);
			System.out.println("[echo]:Payload:"+payload);
			//Product product = (Product) DeserializationHelper.getInstantiedObjectOf(Product.class, payload);
			//System.out.println("[echo]:"+product);
		} catch (IllegalArgumentException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

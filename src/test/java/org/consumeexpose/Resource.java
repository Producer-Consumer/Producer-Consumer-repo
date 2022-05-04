package org.consumeexpose;

import org.consumeexpose.annotations.NoConceal;
import org.consumeexpose.annotations.Producer;

@Producer
public class Resource {

	public int name;
	 String address;

	
	public  String testMethod(int number,String name) {
		System.out.println("[echo]:The method had been called");
		return number+" "+name;
	}
	
	
	public static void anotherMethod() throws CustomException {
		
		System.out.println("[echo]:Hello there!");
		
		Object obj = null;
		
		
		
		
	}
	
	
	
}

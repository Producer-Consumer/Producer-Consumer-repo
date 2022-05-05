package org.consumeexpose;

import org.consumeexpose.annotations.Placeholder;
import org.consumeexpose.annotations.Producer;

@Producer
public class Resource {
	
	
	public int name;
	 String address;

	
	public  String testMethod(@Placeholder(value="Id of the Object")int number,@Placeholder(value="Name of the object")String name) {
		System.out.println("[echo]:The method had been called");
		return number+" "+name;
	}
	
	
	public static License anotherMethod(License license){
		
		
		return license;
		
		
	}
	
	
	
}

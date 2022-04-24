package org.consumeexpose;

import java.util.HashMap;

import org.consumeexpose.annotations.Alias;
import org.consumeexpose.annotations.Expose;
import org.consumeexpose.annotations.Get;
import org.consumeexpose.annotations.Header;
import org.consumeexpose.annotations.Producer;

@Producer
public class Resource {

	public int name;
	 String address;
	 ConsumerImpl consumer;
	public Resource(int name, String address, ConsumerImpl consumer) {
		super();
		this.name = name;
		this.address = address;
		this.consumer = consumer;
	}
	public Resource(String address,int name) {
		super();
		this.name = name;
		this.address = address;
	}
	public Resource(int name) {
		super();
		this.name = name;
	}
	
	public static void testMethod(String abc,@Header HashMap<?,?> header, int number) {
		
	}
	
	public void updateRecord(int number) {
		
	}
	
	public void deleteRecord(int number,int status) {
		
	}
	
	public void recordPurge(int record) {
		
	}
	
	
}

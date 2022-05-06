package org.consumeexpose;

import org.consumeexpose.annotations.Producer;

@Producer
public class About {

	
	public String info(int value) {
		return value+"";
	}
	
	public void calculateWeight(int value1,int value2) {
	
		System.out.println("Hello there!");
	}
	
}

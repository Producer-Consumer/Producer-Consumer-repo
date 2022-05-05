package org.consumeexpose.sub;

import org.consumeexpose.annotations.Producer;

@Producer
public class AnotherResource {

	public String greet(int number) {
		return number+"";
	}
	
}

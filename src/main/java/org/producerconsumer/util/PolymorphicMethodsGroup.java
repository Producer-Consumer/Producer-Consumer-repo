package org.producerconsumer.util;

import java.util.ArrayList;
import java.util.Iterator;

public class PolymorphicMethodsGroup implements Iterable<PolymorphicMethods>{

	private ArrayList<PolymorphicMethods> polymorphicMethods;
	
	public PolymorphicMethodsGroup() {
		this.polymorphicMethods = new ArrayList<PolymorphicMethods>();
	}
	
	public void add(PolymorphicMethods polymorphicMethod) {
		this.polymorphicMethods.add(polymorphicMethod);
	}
	
	@Override
	public Iterator<PolymorphicMethods> iterator() {
		return polymorphicMethods.iterator();
	}

}

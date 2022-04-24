package org.consumeexpose;

public class Product {
	
	int number;
	String identifier;
	ColorWay colorWay;
	
	public Product(int number, String identifier, ColorWay colorWay) {
		super();
		this.number = number;
		this.identifier = identifier;
		this.colorWay = colorWay;
	}
	public Product(int number, String identifier) {
		super();
		this.number = number;
		this.identifier = identifier;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	@Override
	public String toString() {
		return "Product [number=" + number + ", identifier=" + identifier + ", colorWay=" + colorWay + "]";
	}
	
	

}

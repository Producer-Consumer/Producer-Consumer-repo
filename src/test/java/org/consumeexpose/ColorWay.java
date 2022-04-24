package org.consumeexpose;

public class ColorWay {

	int colorNumber;
	String identifier;
	public ColorWay(int colorNumber, String identifier) {
		super();
		this.colorNumber = colorNumber;
		this.identifier = identifier;
	}
	public int getColorNumber() {
		return colorNumber;
	}
	public void setColorNumber(int colorNumber) {
		this.colorNumber = colorNumber;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	@Override
	public String toString() {
		return "ColorWay [colorNumber=" + colorNumber + ", identifier=" + identifier + "]";
	}
	
	
}

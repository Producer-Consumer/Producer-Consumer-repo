package org.consumeexpose;

import org.consumeexpose.annotations.Placeholder;

public class License {
	int licenseNo;
	String identifier;
	
	public License(@Placeholder(value = "License Number") int licenseNo, @Placeholder(value="License Identifier")String identifier) {
		this.licenseNo = licenseNo;
		this.identifier = identifier;
	}

	@Override
	public String toString() {
		return "License [licenseNo=" + licenseNo + ", identifier=" + identifier + "]";
	}

	
	
}

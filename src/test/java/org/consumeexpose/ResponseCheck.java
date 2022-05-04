package org.consumeexpose;

import org.consumeexpose.annotations.Null;
import org.consumeexpose.annotations.ResponseCode;
import org.consumeexpose.annotations.ResponsePolicy;
import org.consumeexpose.annotations.VoidType;
import org.consumeexpose.util.DeserializationException;

@ResponsePolicy
public class ResponseCheck {

	@Null
	public static int nullCode = 404;
	@VoidType
	public static int voidCode = 201;

	@ResponseCode(value=404)
	public static DeserializationException deserializationException;
	
}

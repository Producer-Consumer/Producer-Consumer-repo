package org.consumeexpose.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FilterType {
	
	public static final String DEFAULT_PATH = "";
int order();
String path() default DEFAULT_PATH;
}

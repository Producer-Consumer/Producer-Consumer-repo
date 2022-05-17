package org.producerconsumer.util;

import javax.servlet.Filter;

import org.producerconsumer.MemoryHeap;
import org.producerconsumer.annotations.FilterType;

public class FilterHelper {

	private static MemoryHeap heap = MemoryHeap.getInstance();

	public static void resolveFilters() throws FilterException {

		for (Class<?> filter : heap.filters) {
			if (isFilter(filter)) {
				int order = getOrder(filter);
				if (heap.filtersDefinitions.get(order) == null)
					heap.filtersDefinitions.put(order, filter);
				else
					throw new FilterException("[Order collision]there exists a filter class definition for order:"
							+ order + " with class:" + heap.filtersDefinitions.get(order)
							+ ", Can't add the filter on the same order:" + filter.getName());
				String path = extractPath(filter);
				if(path!=null) {
					heap.filterPaths.put(filter, path);
				}
				
			}
		}

	}
	
	private static String extractPath(Class<?> filterClass) {
		
		if(!filterClass.getAnnotation(FilterType.class).path().equals(FilterType.DEFAULT_PATH)) {
			
			return properPathFor(filterClass.getAnnotation(FilterType.class).path());
		}
		else
			return null;
	}
	
	private static String properPathFor(String path) {
		String properPath = "";
		if(!path.startsWith("/"))
			properPath = "/"+path;
		else
			properPath = path;
		if(!path.endsWith("/*"))
			properPath = properPath+"/*";
		
		return properPath;
	}

	private static boolean isFilter(Class<?> classDefinition) {

		Class<?>[] interfaces = classDefinition.getInterfaces();
		for (Class<?> interfaceDefinition : interfaces)
			if (interfaceDefinition == Filter.class)
				return true;

		return false;
	}

	private static int getOrder(Class<?> classDefinition) {
		return classDefinition.getAnnotation(FilterType.class).order();
	}

}

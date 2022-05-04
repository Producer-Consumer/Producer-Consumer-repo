package org.consumeexpose.util;

import javax.servlet.Filter;

import org.consumeexpose.MemoryHeap;
import org.consumeexpose.annotations.FilterType;

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
			}
		}

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

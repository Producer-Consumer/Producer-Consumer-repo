package org.producerconsumer.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

public class ClassScanner {
	
	public static ArrayList<Class<?>> getClasses(String basePackage) throws ClassNotFoundException{
		
		ArrayList<Class<?>> returnList = new ArrayList<Class<?>>();
		
		
		String[] filesInDir = null;
		
		Stack<String> subDirs = new Stack<String>();
		Stack<String> qualifiedPackages = new Stack<String>();
		qualifiedPackages.add(basePackage);
		String basePackagePath = basePackage.replace(".", "/");
		
		String resourcePath = null;
		
		File workingFile = null;
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		if(classLoader==null) {
			throw new ClassNotFoundException("Can't get class loader");
		}
		
		URL resource = classLoader.getResource(basePackagePath);
		
		if(resource==null) {
			throw new ClassNotFoundException("Can't find resource for specified base package:"+basePackage);
		}
		
		workingFile = new File(resource.getFile());
		
		if(!workingFile.exists()) {
			throw new ClassNotFoundException("Specified base package doesn't exist:"+basePackage);	
		}
		
		subDirs.add(resource.getFile());
		String currentFile = null;
		String qualifiedPackage = null;
		
		while(!subDirs.isEmpty()) {
			resourcePath = subDirs.pop();
			qualifiedPackage = qualifiedPackages.pop();
			workingFile = new File(resourcePath);
			filesInDir = workingFile.list();
			for(int fileIterator=0;fileIterator<filesInDir.length;fileIterator++) {
				currentFile = filesInDir[fileIterator];
				workingFile = new File(resourcePath+File.separator+currentFile);
				if(workingFile.isDirectory()) {
					
					subDirs.add(resourcePath+File.separator+currentFile);
					qualifiedPackages.add(qualifiedPackage+"."+currentFile);
				}
				if(currentFile.endsWith(".class")) {
					returnList.add(Class.forName(qualifiedPackage+"."+currentFile.substring(0,currentFile.length()-6)));
				}
			}
		}
		return returnList;
	}
	
}

package com.teamcenter.soa.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ClassUtil {
	
	@SuppressWarnings("rawtypes") 
	public static Method getAccessibleMethods(Class clazz, String methodToGuess) {
		String correctMethod="get_"+methodToGuess;
	    while (clazz != null) {
	        for (Method method : clazz.getDeclaredMethods()) {
	            int modifiers = method.getModifiers();
	            if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
	            	if(method.getName().equals(correctMethod)){
	            		return method;
	            	}
	            }
	        }
	        clazz = clazz.getSuperclass();
	    }
		return null;	    
	    
	}
}


package com.example.core.cache;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.interceptor.DefaultKeyGenerator;

public class CacheKeyGenerator extends DefaultKeyGenerator {

	@Override
	public Object generate(Object target, Method method, Object... params) {

		Map<Object, Object> key = new HashMap<Object, Object>();
		key.put("keyPart1", params != null ? Arrays.asList(params).toString().hashCode() : -1);
		key.put("keyPart2", method.getDeclaringClass().getName() + "." + method.getName());
		return key;
	}

}

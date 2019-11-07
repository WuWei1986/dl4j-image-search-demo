package com.example.core.datasource;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.aop.MethodBeforeAdvice;

/**
 * @author wuwei
 *
 */
public class BeanNameDatasourceAutoChanger implements MethodBeforeAdvice {
	private Map<String, String> rules;

	public void setRules(Map<String, String> rules) {
		this.rules = rules;
	}

	@Override
	public void before(Method method, Object[] args, Object target) throws Throwable {
		String methodName = method.getName();
		String methodSign = method.getDeclaringClass().getName() + "." + methodName;
		for (String datasourceKey : rules.keySet()) {
			String[] beanNameRegexArray = rules.get(datasourceKey).split(",");
			boolean flag = false;
			for (String beanNameRegex : beanNameRegexArray) {
				if (beanNameRegex != null && Pattern.matches(beanNameRegex.trim(), methodSign)) {
					if (methodName.startsWith("get") || methodName.startsWith("list") || methodName.startsWith("count")) {
						DatasourceHolder.setSlave(datasourceKey);
					} else {
						DatasourceHolder.setMaster(datasourceKey);
					}
					flag = true;
					break;
				}
			}
			if (flag) {
				break;
			}
		}
	}

}

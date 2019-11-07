package com.example.core;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyOverrideConfigurer;

public class BeanRefPropertyOverrideConfigurer extends PropertyOverrideConfigurer {

	private static final String BEAN_REFERENCE_PREFIX = "ref:";

	@Override
	protected void applyPropertyValue(ConfigurableListableBeanFactory factory, String beanName, String property, String value) {
		if (value.startsWith(BEAN_REFERENCE_PREFIX)) {
			String referencedBean = value.substring(BEAN_REFERENCE_PREFIX.length());
			applyBeanReferencePropertyValue(factory, beanName, property, referencedBean);
		} else {
			super.applyPropertyValue(factory, beanName, property, value);
		}
	}

	private void applyBeanReferencePropertyValue(ConfigurableListableBeanFactory factory, String beanName, String property, String referencedBean) {
		BeanDefinition bd = factory.getBeanDefinition(beanName);
		while (bd.getOriginatingBeanDefinition() != null) {
			bd = bd.getOriginatingBeanDefinition();
		}
		Object obj = factory.getBean(referencedBean);
		bd.getPropertyValues().addPropertyValue(property, obj);
	}
}
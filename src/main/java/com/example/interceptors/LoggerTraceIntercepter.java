package com.example.interceptors;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.example.commons.constants.LoggerConstants;

/**
 * @author wuwei
 *
 */
public class LoggerTraceIntercepter extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		MDC.put(LoggerConstants.TRACE_ID, UUID.randomUUID().toString());
		return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		MDC.remove(LoggerConstants.TRACE_ID);
		super.postHandle(request, response, handler, modelAndView);
	}

}

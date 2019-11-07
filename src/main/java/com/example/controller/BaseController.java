package com.example.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.example.core.SessionObject;

public class BaseController {
	
	private static final Logger logger = LoggerFactory.getLogger(BaseController.class);
	protected static final Integer SUCCESS = 1;
	protected static final Integer FAIL = 0;
	
	/**
	 * 重定向
	 * 
	 * @param request
	 * @param response
	 * @param url
	 * @throws IOException
	 */
	public void redirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
		String servletContext = request.getSession().getServletContext().getContextPath();
		response.sendRedirect(servletContext + url);
	}
	
	/**
	 * @param success
	 * @return
	 */
	public static Map<String, Object> response(Integer success) {
		Map<String, Object> output = new HashMap<String, Object>();
		output.put("errCode", "");
		output.put("errMsg", "");
		output.put("success", success);
		String data = JSON.toJSONString(output);
		logger.info("返回数据：{}", data);
		return output;
	}
	
	/**
	 * @param success
	 * @param optional
	 * @return
	 */
	public static Map<String, Object> response(Integer success, Optional<Object> optional) {
		Map<String, Object> output = new HashMap<String, Object>();
		output.put("errCode", "");
		output.put("errMsg", "");
		output.put("success", success);

		output.put("result", optional.orElse(JSON.toJSON(new Object())));
		String data = JSON.toJSONString(output);
		logger.info("返回数据：{}", data);
		return output;
	}
	
	/**
	 * @param success
	 * @param errCode
	 * @param errMsg
	 * @return
	 */
	public static Map<String, Object> response(Integer success, Integer errCode, String errMsg) {
		Map<String, Object> output = new HashMap<String, Object>();
		output.put("errCode", errCode);
		output.put("errMsg", errMsg);
		output.put("success", success);

		String data = JSON.toJSONString(output);
		logger.info("返回数据：{}", data);
		return output;
	}
	
	/**
	 * @param success
	 * @param errCode
	 * @param errMsg
	 * @param optional
	 * @return
	 */
	public static Map<String, Object> response(Integer success, Integer errCode, String errMsg, Optional<Object> optional) {
		Map<String, Object> output = new HashMap<String, Object>();
		output.put("errCode", errCode);
		output.put("errMsg", errMsg);
		output.put("success", success);

		output.put("result", optional.orElse(JSON.toJSON(new Object())));
		String data = JSON.toJSONString(output);
		logger.info("返回数据：{}", data);
		return output;
	}
	
	/**
	 * 获取sessionAttribute
	 * 
	 * @param key
	 * @param request
	 * @param response
	 * @return
	 */
	public Object getSessionAttribute(String key, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		Object object = request.getSession().getAttribute(key);
		// 对有过期时间的对象进行处理，已过期返回null,没过期返回原对象
		if(object != null && object instanceof SessionObject){
			SessionObject sessionObject = (SessionObject)object;
			if(((int)(new Date().getTime()/1000)>sessionObject.getTimestamp())){
				return null;
			}else{
				return sessionObject.getObject();
			}
		}
		return object;
	}

	/**
	 * 保存sessionAttribute
	 * 
	 * @param key
	 * @param value
	 * @param request
	 * @param response
	 */
	public void setSessionAttribute(String key, Object value, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isNotEmpty(key)) {
			request.getSession().setAttribute(key, value);
		}
	}

	/**
	 * 保存sessionAttribute并设置过期时间（单位：秒）
	 * 
	 * @param key
	 * @param value
	 * @param expire
	 * @param request
	 * @param response
	 */
	public void setSessionAttribute(String key, Object value, int expire, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isNotEmpty(key)) {
			request.getSession().setAttribute(key, new SessionObject(value, (int)(new Date().getTime()/1000)+expire));
		}
	}

	/**
	 * 清除sessionAttribute
	 * 
	 * @param key
	 * @param request
	 * @param response
	 */
	public void removeSessionAttribute(String key, HttpServletRequest request, HttpServletResponse response) {
		if (StringUtils.isNotEmpty(key)) {
			request.getSession().removeAttribute(key);
		}
	}
	
	/**
	 * 获取请求参数 
	 * 
	 * @param request
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getRequestParamMap(HttpServletRequest request) {
		Map<String, String[]> srcParamMap = request.getParameterMap();
		Map<String, Object> paramMap = new HashMap<String, Object>();

		for (String key : srcParamMap.keySet()) {
			if (srcParamMap.get(key).length == 1) {
				String value = srcParamMap.get(key)[0];
				paramMap.put(key, StringUtils.trimToEmpty(value));
			} else {
				paramMap.put(key, srcParamMap.get(key));
			}
		}
		return paramMap;
	}
	
}

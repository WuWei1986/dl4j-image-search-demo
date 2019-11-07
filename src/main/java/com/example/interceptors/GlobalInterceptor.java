package com.example.interceptors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

@Controller
public class GlobalInterceptor implements HandlerInterceptor {
	
	private List<String> skipUrl;
	private static final Pattern PSQL = Pattern.compile("\\b(and|xor|or)\\b.+?(<|=|>|\\bin\\b|\\blike\\b|\\bhaving\\b|\\bsleep\\b|\\bbenchmark\\b)|\\/\\*.+?\\*\\/|union.+?select|(select|delete).+?from|insert\\s+into.+?values|update.+?set|(create|alter|drop|truncate)\\s+(table|database)|\\b(order|group).+?by\\b|\\bcase.+?when\\b|\\bload_file\\b|\\binto\\s+outfile",Pattern.CASE_INSENSITIVE);
	private static final Pattern PXSS = Pattern.compile("<[A-Za-z]|<\\s*script\\b|<\\s*img\\b.+?(javascript|onerror)|<\\s*iframe\\b|style=.+?expression|(onload|onfocus|onclick|onmousedown|onmousemove|onmouseout|onmouseover|onmouseup)=",Pattern.CASE_INSENSITIVE);

	@SuppressWarnings("unchecked")
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// 添加无需拦截的链接的配置
		String uri = request.getRequestURI().toString();
		if (matches(skipUrl, uri)) {
			return true;
		}	

		Map<String, String[]> srcParamMap = request.getParameterMap();
		for (String key : srcParamMap.keySet()) {
			String[] strarray = srcParamMap.get(key);
			for(String str : strarray){
				Matcher msql=PSQL.matcher(str);
				Matcher mxss=PXSS.matcher(str);
				boolean rssql = msql.find(); 
				boolean rsxss = mxss.find(); 
				if(rssql || rsxss){
					if (isAjaxRequest(request)) {
						Map<String, Object> data = new HashMap<String, Object>();
						data.put("msg", "输入有非法字符!");
						response.setCharacterEncoding("UTF-8");
						response.setContentType("application/json");
						response.getWriter().write(JSONObject.toJSONString(data));
						return false;
					}else{
						response.sendRedirect("/error.htm");
						return false;
					}

				}
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
		if(null == modelAndView){
			modelAndView = new ModelAndView();
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

	}

	public List<String> getSkipUrl() {
		return skipUrl;
	}

	public void setSkipUrl(List<String> skipUrl) {
		this.skipUrl = skipUrl;
	}

	public static boolean isAjaxRequest(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}
	
	/** 检查是否匹配正则表达式
	 * @param regex 待匹配的正则表达式集合
	 * @param target 待检查的字符串
	 * @return
	 */
	public static boolean matches(String regex, String target) {
		boolean tmp = false;
		if (target.matches(regex)) {
			tmp = true;
		}
		return tmp;
	}
	
	/** 检查是否匹配正则表达式
	 * @param regexList 待匹配的正则表达式集合
	 * @param target 待检查的字符串
	 * @return
	 */
	public static boolean matches(List<String> regexList, String target) {
		boolean tmp = false;
		for (String regex : regexList) {
			if (target.matches(regex)) {
				tmp = true;
				break;
			}
		}
		return tmp;
	}

}

package com.example.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GlobalController extends BaseController{

	/**
	 * 健康检查，启动检查 使用
	 *
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/monitortest", method = RequestMethod.GET)
	public ModelAndView monitortest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.getWriter().write("ok");
		return null;
	}

}

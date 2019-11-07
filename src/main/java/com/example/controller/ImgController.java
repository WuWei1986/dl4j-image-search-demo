package com.example.controller;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.example.commons.constants.Constants;

/**
 * 图片
 * 
 * @author wuwei
 *
 */
@Controller
public class ImgController extends BaseController {

	@RequestMapping(value = "img/product/{fileName:.+}", method = RequestMethod.GET)
	public ModelAndView productImg(HttpServletRequest request, HttpServletResponse response,@PathVariable String fileName) throws Exception {
		if (StringUtils.isBlank(fileName)) {
			return null;
		}
		response.setContentType("image/jpeg");
		BufferedImage bi = ImageIO.read(new File(Constants.productImageDirPath+"/"+fileName));
		if (bi == null) {
			return null;
		}
		ServletOutputStream out = response.getOutputStream();
		ImageIO.write(bi, "jpg", out);
		try {
			out.flush();
		} finally {
			out.close();
		}
		return null;
	}
}

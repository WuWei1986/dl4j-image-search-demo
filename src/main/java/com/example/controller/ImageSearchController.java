package com.example.controller;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.example.business.IImageSearchBusiness;
import com.example.commons.constants.Constants;
import com.example.entity.ProductExt;

/**
 * 图像检索
 * 
 * @author wuwei
 *
 */
@Controller
public class ImageSearchController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(ImageSearchController.class);

	@Autowired
	private IImageSearchBusiness imageSearchBusiness;
	
	/**
	 * 首页
	 * 
	 * @return
	 */
	@RequestMapping(value="index", method = RequestMethod.GET)
	public String index(){
		return "/index";
	}
	
	/**
	 * 根据图片搜索
	 * 
	 * @param modelMap
	 * @param upfile
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	@RequestMapping(value="imageSearch", method = RequestMethod.POST)
	public String imageSearch(ModelMap modelMap, MultipartFile upfile) throws IllegalStateException, IOException {
		if (upfile != null && upfile.getSize() > 0) {
			String originalFilename = upfile.getOriginalFilename();
			logger.info(originalFilename + "/" + upfile.getSize());
			String fileDir = generateFileDir();
			String fileName = generateFileName(originalFilename);
			File dir = new File(Constants.searchImageDirPath + "/" + fileDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File dest = new File(Constants.searchImageDirPath + "/" + fileDir+ "/" + fileName);
			logger.info(dest.getAbsolutePath());
			upfile.transferTo(dest);
			List<ProductExt> productList = imageSearchBusiness.listProduct(dest);
			modelMap.put("productList", productList);
		}
		return "/product/list";
	}
	
	/**
	 * 生成图片名称
	 * 
	 * @param originalFilename
	 * @return
	 */
	private String generateFileName(String originalFilename) {
		String uuid = UUID.randomUUID().toString();
		uuid = uuid.replaceAll("-", "");
		String fileName = uuid + "." + originalFilename.substring(originalFilename.lastIndexOf(".") + 1, originalFilename.length());
		return fileName;
	}

	/**
	 * 生成图片路径
	 * 
	 * @return
	 */
	private String generateFileDir() {
		Calendar now = Calendar.getInstance();
		DecimalFormat decimalFormat = new DecimalFormat("00");
		String fileDir = now.get(Calendar.YEAR) + "/" + decimalFormat.format(now.get(Calendar.MONTH) + 1) + decimalFormat.format(now.get(Calendar.DATE));
		return fileDir;
	}
}

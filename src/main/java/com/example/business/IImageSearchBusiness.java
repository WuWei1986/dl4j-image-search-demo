package com.example.business;

import java.io.File;
import java.util.List;

import com.example.entity.ProductExt;

public interface IImageSearchBusiness {
	/**
	 * 根据图片搜索
	 * 
	 * @param file
	 * @return
	 */
	List<ProductExt> listProduct(File file);
}

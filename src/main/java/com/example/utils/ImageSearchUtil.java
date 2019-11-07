package com.example.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.commons.constants.Constants;
import com.example.entity.ProductExt;

public class ImageSearchUtil {
	
	/**
	 * 从文件里获取商品信息
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException 
	 */
	public static ProductExt getProduct(String fileName) throws IOException {
		ProductExt product = new ProductExt();
		File file = new File(Constants.productDataDirPath+"/"+fileName.split("\\.")[0]+".txt");
		String productJsonStr = getProductData(file);
		if (StringUtils.isBlank(productJsonStr)) {
			product.setId(fileName.split("\\.")[0]);
		} else {
			JSONObject prodcutJsonObj = JSON.parseObject(productJsonStr);
			product.setTitle(prodcutJsonObj.getString("title"));
			if (StringUtils.isNotBlank(prodcutJsonObj.getString("price"))) {
				product.setPrice(new BigDecimal(prodcutJsonObj.getString("price")));
			}
			product.setPhash(prodcutJsonObj.getString("phash"));
			product.setSourceId(prodcutJsonObj.getInteger("sourceId"));
		}
		return product;
	}
	
	public static String getProductData(File file) throws IOException {
		if (file.exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = br.readLine();
			br.close();
			return line;
		}
		return null;
	}
	
	public static void saveProduct(ProductExt prodcut) throws IOException {
		File file = new File(Constants.productDataDirPath+"/"+prodcut.getId()+".txt");
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
		bw.write(JSON.toJSONString(prodcut));
		bw.close();
	}
	
	/**
	 * 计算汉明距离
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static int hanmingDistance(String first, String second){
		int d = 0;
		for (int i=0; i<first.length(); i++) {
			if (first.charAt(i) != second.charAt(i)) {
				d ++;
			}
		}
		return d;
	}
	
	/**
	 * 余弦夹角距离
	 * 
	 * @param first
	 * @param second
	 * @return
	 */
	public static double cosDistance(float[] first, float[] second){
		float sum = 0;
		float squareSum1 = 0;
		float squareSum2 = 0;
		for (int i=0; i<first.length; i++) {
			sum += first[i]*second[i];
			squareSum1 += first[i]*first[i];
			squareSum2 += second[i]*second[i];
		}
		return sum/(Math.sqrt(squareSum1*squareSum2));
	}
}

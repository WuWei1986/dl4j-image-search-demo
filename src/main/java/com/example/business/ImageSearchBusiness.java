package com.example.business;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.example.commons.constants.Constants;
import com.example.core.LSH;
import com.example.core.PHash;
import com.example.core.ignite.IgniteCacheManager;
import com.example.core.neuralNetwork.ImageEncodeAPI;
import com.example.entity.ProductExt;
import com.example.utils.ImageSearchUtil;

/**
 * 图像检索业务层
 * 
 * @author wuwei
 *
 */
public class ImageSearchBusiness implements IImageSearchBusiness{
	private static final Logger logger = LoggerFactory.getLogger(ImageSearchBusiness.class);
	
	private static final String IMAGE_SEARCH_INIT_LOCK_KEY = "imageSearchInitLock";
	private static final String LSH_RANDOMNUMS_KEY = "lshRandomNums";
	private static final int dimention = 100;
	private static final int max = 200;
	private static final int hashCount = 100; 
	private static final int bitCount = 60;
	
	@Autowired
	private IgniteCacheManager igniteCacheManager;
	private LSH lsh;
	
	/**
	 * 局部敏感hash数据初始化
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void init() throws IOException {
		logger.info("ImageSearchBusiness init...");
		List<Integer> lshRandomNums = null;
		Lock lock = igniteCacheManager.getLock().lock(IMAGE_SEARCH_INIT_LOCK_KEY);
		lock.lock();
		logger.info("ImageSearchBusiness init lock...");
		try {
			if (!igniteCacheManager.getProductCache().containsKey(LSH_RANDOMNUMS_KEY)) {
				lshRandomNums = getLshRandomNums(hashCount, bitCount, dimention*max);
				igniteCacheManager.getProductCache().put(LSH_RANDOMNUMS_KEY, JSON.toJSONString(lshRandomNums));
			} else {
				logger.info("get randomNums from cache");
				lshRandomNums = JSON.parseArray(igniteCacheManager.getProductCache().get(LSH_RANDOMNUMS_KEY), Integer.class);
			}
		} catch (Exception e) {
			logger.error("init lshRandomNums error",e);
		}
		lock.unlock();
		logger.info("ImageSearchBusiness init unlock");
		lsh = new LSH(dimention,max,hashCount,bitCount,lshRandomNums);
		File[] files = new File(Constants.productImageDirPath).listFiles();
		for (File file:files) {
			try {
				float[] features = ImageEncodeAPI.encode(file);
				ProductExt product = ImageSearchUtil.getProduct(file.getName());
				product.setImgUrl("img/product/"+file.getName());
				lsh.generateHashMap(String.valueOf(product.getId()), formatFeatures(features));
				product.setFeatures(features);
				if (!igniteCacheManager.getProductCache().containsKey(product.getId())) {
					if (product.getPhash() == null) {
						//String phash = PHash.phash(Constants.productImageDirPath+"/"+file.getName());
						//product.setPhash(phash);
					}
					igniteCacheManager.getProductCache().put(product.getId(), JSON.toJSONString(product));
				}
			} catch (Exception e) {
				logger.error("init error file "+file.getName(),e);
			}
		}
		logger.info("ImageSearchBusiness init end ");
	}
	
	/**
	 * 获取lsh随机投影点
	 * 
	 * @param hashCount
	 * @param bitCount
	 * @param size
	 * @return
	 */
	private List<Integer> getLshRandomNums(int hashCount, int bitCount, int size) {
		List<Integer> list = new ArrayList<>();
		Random rd = new Random();
		for (int i = 0; i < hashCount; i++) {
			for (int j = 0; j < bitCount; j++) {
				list.add(rd.nextInt(size));
			}
		}
		return list;
	}
	
	/**
	 * 格式化特征值
	 * 
	 * @param features
	 * @return
	 */
	private static int[] formatFeatures(float[] features) {
		int[] intArray = new int[features.length];
		for (int i=0;i<features.length;i++) {
			intArray[i] = (int) (features[i] * 100) + 100;
			if (intArray[i] > 200) {
				intArray[i] = 200;
			}
			if (intArray[i] < 0) {
				intArray[i] = 0;
			}
		}
		return intArray;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<ProductExt> listProduct(File file) {
		List<ProductExt> productList = new ArrayList<>();
		try {
			float[] features = ImageEncodeAPI.encode(file);
			Set<String> productIds = lsh.query(formatFeatures(features));
			if (productIds == null) {
				return productList;
			}
			Map<Double,List<ProductExt>> treeMap = new TreeMap<>();
			for (String id:productIds) {
				ProductExt product = JSON.parseObject(igniteCacheManager.getProductCache().get(id), ProductExt.class);
				//int d = ImageSearchUtil.hanmingDistance(PHash.phash(file.getAbsolutePath()),product.getPhash());
				double d = ImageSearchUtil.cosDistance(features, product.getFeatures());
				List<ProductExt> tempList = treeMap.get(d);
				if (tempList == null) {
					tempList = new ArrayList<>();
					treeMap.put(d, tempList);
				}
				tempList.add(product);
			}
			treeMap = ((TreeMap)treeMap).descendingMap();
			for (Map.Entry<Double,List<ProductExt>> entry: treeMap.entrySet()) {
				//if (entry.getKey() <= 5) {
				//	productList.addAll(entry.getValue());
				//}
				if (Math.abs(entry.getKey()) > 0.6) {
					productList.addAll(entry.getValue());
				}
			}
		} catch (Exception e) {
			logger.error("listProduct error", e);
		}
		System.out.println(productList.size());
		if (productList.size() > 20) {
			return productList.subList(0, 20);
		}
		return productList;
	}
	
}

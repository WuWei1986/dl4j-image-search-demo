package com.example.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LSH {
	private static final Logger logger = LoggerFactory.getLogger(LSH.class);
	private int dimention; // 维度大小
	private int max; // 所需向量中元素可能的上限
	private int hashCount; // 哈希表的数量，用于更大程度地削减false positive
	// LSH随机选取的采样位数，该值越小，则近似查找能力越大，但相应的false
	// positive也越大；若该值等于size，则为由近似查找退化为精确匹配
	private int bitCount;
	private int size; // 转化为01字符串之后的位数，等于max乘以dimensions
	private int[][] hashFamily; // LSH哈希族，保存了随机采样点的INDEX
	private Map<String, ArrayList<String>> map;

	public LSH(int dimention, int max, int hashCount, int bitCount) {
		this.dimention = dimention;
		this.max = max;
		this.hashCount = hashCount;
		this.bitCount = bitCount;
		this.size = this.dimention * this.max;
		this.hashFamily = new int[hashCount][bitCount];
		map = new HashMap<>();
		generataHashFamily();
	}
	
	public LSH(int dimention, int max, int hashCount, int bitCount, List<Integer> randomNums) {
		this.dimention = dimention;
		this.max = max;
		this.hashCount = hashCount;
		this.bitCount = bitCount;
		this.size = this.dimention * this.max;
		this.hashFamily = new int[hashCount][bitCount];
		map = new HashMap<>();
		generataHashFamily(randomNums);
	}

	// 生成随机的投影点
	private void generataHashFamily() {
		Random rd = new Random();
		for (int i = 0; i < hashCount; i++) {
			for (int j = 0; j < bitCount; j++) {
				hashFamily[i][j] = rd.nextInt(size);
			}
		}
	}
	
	// 生成随机的投影点
	private void generataHashFamily(List<Integer> randomNums) {
		int k = 0;
		for (int i = 0; i < hashCount; i++) {
			for (int j = 0; j < bitCount; j++) {
				hashFamily[i][j] = randomNums.get(k);
				k++;
			}
		}
	}

	// 将向量转化为二进制字符串，比如元素的最大范围255，则元素65就被转化为65个1以及190个0
	public int[] hanmingEncode(int[] data) {
		int unArayData[] = new int[size];
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i]; j++) {
				unArayData[i * max + j] = 1;
			}
		}
		return unArayData;
	}

	// 将向量映射为LSH中的key
	private String generateHashKey(int[] vercotr, int hashNum) {
		StringBuilder sb = new StringBuilder();
		int[] tempData = hanmingEncode(vercotr);
		int[] hashedData = new int[bitCount];
		// 首先将向量转为二进制字符串
		for (int i = 0; i < bitCount; i++) {
			hashedData[i] = tempData[hashFamily[hashNum][i]];
			sb.append(hashedData[i]);
			// System.out.print(hashedData[i]);
		}
		return sb.toString();
	}

	// 将向量映射为LSH中的key，并保存至map中
	public void generateHashMap(String id, int[] vercotr) {
		for (int j = 0; j < hashCount; j++) {
			String key = generateHashKey(vercotr, j);
			//System.out.println(key);
			ArrayList<String> l = map.get(key);
			if (map.containsKey(key)) {
				l = map.get(key);
			} else {
				l = new ArrayList<String>();
				map.put(key, l);
			}
			l.add(id);
			logger.info(id+"-lsh:"+key);
		}
	}

	// 查询与输入向量最接近（海明空间）的向量
	public Set<String> query(int[] data) {
		Set<String> result = new HashSet<String>();
		for (int j = 0; j < hashCount; j++) {
			String key = generateHashKey(data, j);
			if (map.containsKey(key)) {
				result.addAll(map.get(key));
			}
			logger.info("-lsh:"+key);
		}
		return result;
	}
	
}

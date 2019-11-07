package com.example.core;

import static org.bytedeco.javacpp.opencv_core.CV_32F;
import static org.bytedeco.javacpp.opencv_core.abs;
import static org.bytedeco.javacpp.opencv_core.dct;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;

public class PHash {
	public static String phash(String filePath) {
		//System.out.println(filePath);
		Mat src = imread(filePath, 0);
		resize(src, src, new Size(32,32));
		src.convertTo(src, CV_32F);
		dct(src, src);
		src = abs(src).asMat();
		int sum = 0;
		FloatRawIndexer srcIndexer = src.createIndexer();
	    for (int i = 0; i < 8; i++) {
	        for (int j = 0; j < 8; j++) {
	            sum += (int)srcIndexer.get(i, j);
	        }
	    }
	    double average = sum/64;
	    //int[][] phashcode = new int[8][8];
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < 8; i++) {
	        for (int j = 0; j < 8; j++) {
	        	//phashcode[i][j] = srcIndexer.get(i, j) > average ? 1:0;
	        	sb.append(srcIndexer.get(i, j) > average ? "1":"0");
	        }
	    }
	    return sb.toString();
	}
}

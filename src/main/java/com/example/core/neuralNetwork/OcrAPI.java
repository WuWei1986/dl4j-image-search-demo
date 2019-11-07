package com.example.core.neuralNetwork;

import java.io.File;
import java.io.IOException;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.concurrency.AffinityManager;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.example.commons.constants.OcrConstants;

/**
 * 文字识别API
 * 
 * @author wuwei
 *
 */
public class OcrAPI {

	private static ComputationGraph model;

	static {
		try {
			model = ModelSerializer.restoreComputationGraph(OcrConstants.modelFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 预测
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String predict(File file) throws IOException {
		NativeImageLoader loader = new NativeImageLoader(OcrConstants.height, OcrConstants.width, OcrConstants.channels);
		INDArray image = loader.asMatrix(file);
		Nd4j.getAffinityManager().ensureLocation(image, AffinityManager.Location.DEVICE);
		image =  image.muli(1.0/255.0);
		INDArray[]  output = model.output(true, image);
		String text = "";
		INDArray preOutput = null;
		for (int digit = 0; digit < (int) output[0].shape()[2]; digit++) {
			preOutput = output[0].getRow(0).getColumn(digit);
			preOutput.putScalar(new int[] { 0, 0 }, 0);
			text += OcrConstants.textChars.charAt((Nd4j.argMax(preOutput, 0).getInt(0)));
		}
		text = simpleParseOutput(text);
		return text;
	}
	
	/**
	 * 简单处理预测结果
	 * 每个时序除去blank后最大的做为该时序的预测结果
	 * 
	 * @param out
	 * @return
	 */
	public static String simpleParseOutput(String out) {
		StringBuilder sb = new StringBuilder();
		char temp = out.charAt(0);
		sb.append(temp);
		for (int i=1;i<out.length();i++) {
			if (out.charAt(i) != temp) {
				sb.append(out.charAt(i));
				temp = out.charAt(i);
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
		OcrAPI ocrAPI = new OcrAPI();
		String text = ocrAPI.predict(new File("E:/deeplearning/captcha-x/train/2c42ad.jpg"));
		System.out.println(text);
	}
}

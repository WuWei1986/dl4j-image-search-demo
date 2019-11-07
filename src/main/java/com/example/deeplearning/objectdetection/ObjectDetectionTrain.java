package com.example.deeplearning.objectdetection;

import java.io.IOException;

public class ObjectDetectionTrain {
	public static void main(String[] args) {
		double[][] priorBoxes ={{0.10, 0.70}, {0.12, 0.21},{0.12,1.82},{0.27,1.22}, {0.30, 0.53}};
		ObjectDetection objectDetection = new ObjectDetection.Builder()
				.setnEpochs(100)
				.setBatchSize(10)
				.setDataSetType("train")
				.setnClasses(1)
				.setLearningRate(1e-4)
				.setDetectionThreshold(0.5)
				.setPriorBoxes(priorBoxes)
				.setDirPath("E:/deeplearning/fashionImage/")
				.setModelFileName("textDetectionModel")
				.toObjectDetection();
		try {
			objectDetection.testTextXY();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			objectDetection.train();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			objectDetection.predict();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

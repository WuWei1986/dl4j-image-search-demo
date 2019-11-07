package com.example.core.neuralNetwork;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import org.bytedeco.javacpp.opencv_core.Point;
import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.objdetect.DetectedObject;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.concurrency.AffinityManager;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;

import com.example.commons.constants.ObjectDetectionConstants;

/**
 * 文本检测API
 * 
 * @author wuwei
 *
 */
public class TextDetectionAPI {
	private static ComputationGraph model;

	static {
		try {
			model = ModelSerializer.restoreComputationGraph(ObjectDetectionConstants.modelFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 文本检测
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static List<Point[]> predict(File file) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(file);
        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();
		NativeImageLoader loader = new NativeImageLoader(ObjectDetectionConstants.height, ObjectDetectionConstants.width, ObjectDetectionConstants.nChannels);
		INDArray image = loader.asMatrix(file);
		Nd4j.getAffinityManager().ensureLocation(image, AffinityManager.Location.DEVICE);
		new ImagePreProcessingScaler(0, 1).preProcess(image);
		org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer yout =
                (org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer)model.getOutputLayer(0);
		INDArray results = model.outputSingle(image);
        List<DetectedObject> objs = yout.getPredictedObjects(results, ObjectDetectionConstants.detectionThreshold);
        List<Point[]> rects = new ArrayList<>();
        for (DetectedObject obj : objs) {
            double[] xy1 = obj.getTopLeftXY();
            double[] xy2 = obj.getBottomRightXY();
            int x1 = (int) Math.round(w * xy1[0] / ObjectDetectionConstants.gridWidth);
            int y1 = (int) Math.round(h * xy1[1] / ObjectDetectionConstants.gridHeight);
            int x2 = (int) Math.round(w * xy2[0] / ObjectDetectionConstants.gridWidth);
            int y2 = (int) Math.round(h * xy2[1] / ObjectDetectionConstants.gridHeight);
            Point[] rect = new Point[2];
            rect[0] = new Point(x1, y1);
            rect[1] = new Point(x2, y2);
            rects.add(rect);
        }
        rects = merge(rects);
		return rects;
	}
	
	/**
	 * 合并预测框
	 * 
	 * @param rects
	 * @return
	 */
	private static List<Point[]> merge(List<Point[]> rects) {
		List<Point[]> result = new ArrayList<>();
		while(rects.size() > 0) {
			Point[] temp = new Point[2];
			temp[0] = new Point(rects.get(0)[0].x(), rects.get(0)[0].y());
			temp[1] = new Point(rects.get(0)[1].x(), rects.get(0)[1].y());
			Iterator<Point[]> it = rects.iterator();
			while (it.hasNext()) {
				Point[] rect = it.next();
				if (temp[1].y() <= rect[1].y()) {
					if (!((double)(temp[1].y()-rect[0].y())/(temp[1].y()-temp[0].y()) > 0.7)) {
						continue;
					}
					int y1 =  rect[0].y() <  temp[0].y() ? rect[0].y() : temp[0].y();
					temp[0].y(y1);
					temp[1].x(rect[1].x());
					temp[1].y(rect[1].y());
					it.remove();
				} else {
					if (!((double)(rect[1].y()-temp[0].y())/(temp[1].y()-temp[0].y()) > 0.7)) {
						continue;
					}
					int y1 =  rect[0].y() <  temp[0].y() ? rect[0].y() : temp[0].y();
					temp[0].y(y1);
					temp[1].x(rect[1].x());
					it.remove();
				}
			}
			temp[0].x(temp[0].x()-16);
			temp[1].x(temp[1].x()+16);
			result.add(temp);
		}
		return result; 
	}
}

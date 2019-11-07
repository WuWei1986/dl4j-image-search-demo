package com.example.core;

import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Rect;
import org.bytedeco.javacpp.opencv_features2d.Feature2D;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;

/**
 * javacv工具类
 * 
 * @author wuwei
 *
 */
public class JavacvUtil {
	
	/**
	 * 显示图片
	 * 
	 * @param mat
	 * @param title
	 */
	public static void showImage(Mat mat, String title) {
		ToMat converter = new OpenCVFrameConverter.ToMat();
		CanvasFrame canvas = new CanvasFrame(title, 1);
		canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.showImage(converter.convert(mat));
	}

	/**
	 * 保存图片
	 * 
	 * @param mat
	 * @param filePath
	 * @return
	 */
	public static boolean writeImage(Mat mat, String filePath) {
		try {
			/**
			 * 将mat转为java的BufferedImage
			 */
			ToMat convert = new ToMat();
			Frame frame = convert.convert(mat);
			Java2DFrameConverter java2dFrameConverter = new Java2DFrameConverter();
			BufferedImage bufferedImage = java2dFrameConverter.convert(frame);
			ImageIO.write(bufferedImage, "JPG", new File(filePath));
			return true;
		} catch (Exception e) {
			System.out.println("保存文件出现异常:" + filePath);
			e.printStackTrace();
		}
		return false;
	}
	
	public static Mat iplImageToMat(IplImage iplImage) {
		Mat mat = new Mat(iplImage);
		return mat;
	}
	
	public static IplImage matToIplImage(Mat mat) {
		IplImage iplImage = new IplImage(mat);
		return iplImage;
	}
	
	/**
	 * 获取sift特征，使用不了
	 * 
	 * @param file
	 * @return
	 */
	public static Mat getSiftFeature(File file) {
		Mat mat = imread(file.getAbsolutePath(), CV_BGR2GRAY);
		Mat descriptors = new Mat();
		KeyPointVector keypoints = new KeyPointVector();
		Feature2D feature2D = new Feature2D();
		feature2D.detect(mat, keypoints);
		feature2D.compute(mat, keypoints, descriptors);
		feature2D.close();
		return descriptors;
	}
	
	/**
	 * 批量预处理
	 * 
	 * @param dirPath
	 * @param targetDirPath
	 */
	@SuppressWarnings("unused")
	private static void batchPreProcess(String dirPath, String targetDirPath) {
		File dir = new File(dirPath);
		File[] files = dir.listFiles();
		for (File file : files) {
			Mat src = imread(file.getAbsolutePath());
			int x = src.arrayWidth()/2 - 150;
			int y = src.arrayHeight()/2 - 100;
			if (x < 0) {
				x = 0;
			}
			if (y < 0) {
				y = 0;
			}
			int w = (x + 300) > src.arrayWidth() ? src.arrayWidth()-x :300;
			int h = (y + 300) > src.arrayHeight() ? src.arrayHeight() - y : 300;
			Rect rect = new Rect(x, y, w,h);
			Mat roi = new Mat(src, rect);
			writeImage(roi, targetDirPath +"/" + file.getName());
		}
	}
	
	/**
	 * 预处理
	 * 
	 * @param filePath
	 * @param targetPath
	 */
	@SuppressWarnings("unused")
	private static void preProcess(String filePath, String targetPath) {
		Mat src = imread(filePath);
		int x = 20;
		int y = 0;
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}
		int w = src.arrayWidth() -20;
		int h = src.arrayHeight();
		Rect rect = new Rect(x, y, w,h);
		Mat roi = new Mat(src, rect);
		writeImage(roi, targetPath);
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
	}
	

}

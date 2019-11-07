package com.example.deeplearning.autoencoder;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.variational.BernoulliReconstructionDistribution;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;

/**
 * 图片编码
 * 
 * @author wuwei
 *
 */
public class ImageEncoder{

	private static int nEpochs = 1000;
	private static int minibatchSize = 100;
	private static int rngSeed = 12345;
	private static String dirPath;
	private static String modelFileName;
	
	public static int getnEpochs() {
		return nEpochs;
	}

	public static void setnEpochs(int nEpochs) {
		ImageEncoder.nEpochs = nEpochs;
	}

	public static int getMinibatchSize() {
		return minibatchSize;
	}

	public static void setMinibatchSize(int minibatchSize) {
		ImageEncoder.minibatchSize = minibatchSize;
	}

	public static int getRngSeed() {
		return rngSeed;
	}

	public static void setRngSeed(int rngSeed) {
		ImageEncoder.rngSeed = rngSeed;
	}

	public static String getDirPath() {
		return dirPath;
	}

	public static void setDirPath(String dirPath) {
		ImageEncoder.dirPath = dirPath;
	}

	public static String getModelFileName() {
		return modelFileName;
	}

	public static void setModelFileName(String modelFileName) {
		ImageEncoder.modelFileName = modelFileName;
	}

	/**
	 * 训练
	 * 
	 * @throws IOException
	 */
	public static void train() throws IOException {
		Nd4j.getRandom().setSeed(rngSeed);
		MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().seed(rngSeed).updater(new Adam(1e-3))
				.weightInit(WeightInit.XAVIER).l2(1e-4).list()
				.layer(0,
						new VariationalAutoencoder.Builder().activation(Activation.LEAKYRELU)
								.encoderLayerSizes(512,512).decoderLayerSizes(512,512)
								.pzxActivationFunction(Activation.IDENTITY)
								.reconstructionDistribution(new BernoulliReconstructionDistribution(
										Activation.SIGMOID.getActivationFunction()))
								.nIn(100 * 100).nOut(100).build())
				.pretrain(true).backprop(false).build();

		MultiLayerNetwork net = new MultiLayerNetwork(conf);
		net.init();
		System.out.println(net.summary());
		UIServer uiServer = UIServer.getInstance();
		StatsStorage statsStorage = new InMemoryStatsStorage();
		uiServer.attach(statsStorage);
		net.setListeners(new ScoreIterationListener(10), new StatsListener(statsStorage));
		DataSetIterator trainIter = new ImageDataSetIterator(minibatchSize, true, new File(dirPath));
		for (int i = 1; i <= nEpochs; i++) {
			System.out.println("Starting epoch " + i + " of " + nEpochs);
			net.pretrain(trainIter); 
			trainIter.reset();
			// 每100轮保存一次模型
			if ((i) % 10 == 0) {
				ModelSerializer.writeModel(net, modelFileName, true);
				test();
			}
		}
		System.out.println("end...");
	}
	
	/**
	 * 编解码测试
	 * 
	 * @throws IOException
	 */
	public static void test() throws IOException {
		MultiLayerNetwork net = ModelSerializer.restoreMultiLayerNetwork(modelFileName);
		org.deeplearning4j.nn.layers.variational.VariationalAutoencoder vae = (org.deeplearning4j.nn.layers.variational.VariationalAutoencoder) net.getLayer(0);
		DataSet testdata = new ImageDataSetIterator(500, true, new File(dirPath)).next();
		INDArray latentSpaceValues = vae.activate(testdata.getFeatures(), false, LayerWorkspaceMgr.noWorkspaces());
		long[] shap = latentSpaceValues.shape();
    	for (int i=0;i<shap[0];i++){
    		INDArray out = vae.generateAtMeanGivenZ(latentSpaceValues.getRow(i));
    		out = out.muli(255);
    		out = out.reshape(new int[]{100,100});
    		saveImage(out,i);
    	}
    	System.out.println("end...");
	}
	
	/**
	 * 保存图片
	 * 
	 * @param combination
	 * @param iteration
	 * @throws IOException
	 */
	private static void saveImage(INDArray combination, int iteration) throws IOException {
	    BufferedImage output = imageFromINDArray(combination);
	    File file = new File("C:/Users/Administrator/Desktop/test/"+iteration+".jpg");
	    ImageIO.write(output, "jpg", file);
	}
	
	/**
	 * INDArray转为BufferedImage
	 * 
	 * @param array
	 * @return
	 */
	private static BufferedImage imageFromINDArray(INDArray array) {
	    long[] shape = array.shape();

	    int height = (int)shape[0];
	    int width = (int)shape[1];
	    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
	    for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	            int gray = array.getInt(y, x);

	            // handle out of bounds pixel values
	            gray = Math.min(gray, 255);
	            gray = Math.max(gray, 0);

	            image.getRaster().setSample(x, y, 0, gray);
	        }
	    }
	    return image;
	}
	
}

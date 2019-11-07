package com.example.core.neuralNetwork;

import java.io.File;
import java.io.IOException;

import org.datavec.image.loader.NativeImageLoader;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.workspace.LayerWorkspaceMgr;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.commons.constants.ImageEncodeConstants;

/**
 * 图像编码api
 * 
 * @author wuwei
 *
 */
public class ImageEncodeAPI {
	
	private static final Logger logger = LoggerFactory.getLogger(ImageEncodeAPI.class);
	
	private static MultiLayerNetwork model ;
	static {
		try {
			model = ModelSerializer.restoreMultiLayerNetwork(ImageEncodeConstants.modelFilePath);
		} catch (IOException e) {
			logger.error("restoreMultiLayerNetwork error", e);
		}
	}
	
	/**
	 * 编码
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static float[] encode(File file) throws IOException {
		org.deeplearning4j.nn.layers.variational.VariationalAutoencoder vae = (org.deeplearning4j.nn.layers.variational.VariationalAutoencoder) model.getLayer(0);
		NativeImageLoader loader = new NativeImageLoader(ImageEncodeConstants.width, ImageEncodeConstants.height, ImageEncodeConstants.channels);
		INDArray image = loader.asMatrix(file);
		image =  image.muli(1.0/255.0);
		image = image.ravel();
		INDArray latentSpaceValues = vae.activate(image, false, LayerWorkspaceMgr.noWorkspaces());	
		return latentSpaceValues.getRow(0).toFloatVector();
	} 
	
	/**
	 * 解码
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static INDArray decode(File file) throws IOException {
		org.deeplearning4j.nn.layers.variational.VariationalAutoencoder vae = (org.deeplearning4j.nn.layers.variational.VariationalAutoencoder) model.getLayer(0);
		NativeImageLoader loader = new NativeImageLoader(ImageEncodeConstants.width, ImageEncodeConstants.height, ImageEncodeConstants.channels);
		INDArray image = loader.asMatrix(file);
		image =  image.muli(1.0/255.0);
		image = image.ravel();
		INDArray latentSpaceValues = vae.activate(image, false, LayerWorkspaceMgr.noWorkspaces());	
		INDArray out = vae.generateAtMeanGivenZ(latentSpaceValues);
		out = out.muli(255);
		out = out.reshape(new int[]{ImageEncodeConstants.width,ImageEncodeConstants.height});
		return out;
	} 
	
}

package com.example.deeplearning.autoencoder;

public class ImageEncodeTrain{

	public static void main(String[] args) throws Exception {
		ImageEncoder imageEncode = new ImageEncoder();
		imageEncode.setDirPath("E:/deeplearning/fashionImage");
		imageEncode.setModelFileName("imageEncodeModel.json");
		imageEncode.train();
		//imageEncode.test();
	}
	
}

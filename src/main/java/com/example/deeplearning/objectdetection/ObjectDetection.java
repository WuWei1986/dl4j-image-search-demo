package com.example.deeplearning.objectdetection;
import static org.bytedeco.javacpp.opencv_core.CV_8U;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.datavec.api.records.metadata.RecordMetaDataImageURI;
import org.datavec.api.split.FileSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.objdetect.ObjectDetectionRecordReader;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.ConvolutionMode;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.WorkspaceMode;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.layers.objdetect.Yolo2OutputLayer;
import org.deeplearning4j.nn.graph.ComputationGraph;
import org.deeplearning4j.nn.layers.objdetect.DetectedObject;
import org.deeplearning4j.nn.transferlearning.FineTuneConfiguration;
import org.deeplearning4j.nn.transferlearning.TransferLearning;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.deeplearning4j.zoo.model.TinyYOLO;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;

/**
 * 目标检测
 * 
 * @author wuwei
 *
 */
public class ObjectDetection extends NativeImageLoader {
	
	private static final long serialVersionUID = 1L;
	private static final int seed = 123;
	public static final int width = 416;
	public static final int height = 416;
	public static final int nChannels = 3;
	public static final int gridWidth = 13;
	public static final int gridHeight = 13;
	private static final int nBoxes = 5;
	private static final double lambdaNoObj = 0.5;
	private static final double lambdaCoord = 5.0;
	private int nClasses = 1;
	private int batchSize = 10;
	private int nEpochs = 100;
	private String dataSetType = "train";
	private double learningRate = 1e-4;
	public static double detectionThreshold = 0.5;
	private double[][] priorBoxes;
	private String dirPath;
	private String modelFileName;
	
	public int getnClasses() {
		return nClasses;
	}

	public void setnClasses(int nClasses) {
		this.nClasses = nClasses;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public int getnEpochs() {
		return nEpochs;
	}

	public void setnEpochs(int nEpochs) {
		this.nEpochs = nEpochs;
	}

	public String getDataSetType() {
		return dataSetType;
	}

	public void setDataSetType(String dataSetType) {
		this.dataSetType = dataSetType;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public double getDetectionThreshold() {
		return detectionThreshold;
	}

	public void setDetectionThreshold(double detectionThreshold) {
		this.detectionThreshold = detectionThreshold;
	}

	public double[][] getPriorBoxes() {
		return priorBoxes;
	}

	public void setPriorBoxes(double[][] priorBoxes) {
		this.priorBoxes = priorBoxes;
	}

	public String getDirPath() {
		return dirPath;
	}

	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}

	public String getModelFileName() {
		return modelFileName;
	}

	public void setModelFileName(String modelFileName) {
		this.modelFileName = modelFileName;
	}

	/**
	 * 训练
	 * 
	 * @throws IOException
	 */
	public void train () throws IOException{
		File trainDir = new File(dirPath + dataSetType);
		FileSplit trainData = new FileSplit(trainDir, NativeImageLoader.ALLOWED_FORMATS, rng);
		ObjectDetectionRecordReader recordReaderTrain = new ObjectDetectionRecordReader(height, width, nChannels,
                gridHeight, gridWidth, new LaberProvider(trainDir));
		recordReaderTrain.initialize(trainData);
		RecordReaderDataSetIterator trainDataIterator = new RecordReaderDataSetIterator(recordReaderTrain, batchSize, 1, 1, true);
		trainDataIterator.setPreProcessor(new ImagePreProcessingScaler(0, 1));
        ComputationGraph model = createModel();
        System.out.println(model.summary(InputType.convolutional(height, width, nChannels)));
        model.setListeners(new ScoreIterationListener(1));
        for (int i = 0; i < nEpochs; i++) {
            model.fit(trainDataIterator);
            // 每10轮保存一次模型
            if ((i+1) % 10 == 0) {
            	ModelSerializer.writeModel(model, modelFileName, true);
            }
        }
        System.out.println("train end...");
	}
	
	/**
	 * 预测
	 * 
	 * @throws IOException
	 * @throws InterruptedException 
	 */
	public void predict() throws IOException, InterruptedException {
		ComputationGraph model = ModelSerializer.restoreComputationGraph(modelFileName);
		File testDir = new File(dirPath + dataSetType);
		FileSplit testData = new FileSplit(testDir, NativeImageLoader.ALLOWED_FORMATS, rng);
		ObjectDetectionRecordReader recordReaderTrain = new ObjectDetectionRecordReader(height, width, nChannels,
                gridHeight, gridWidth, new LaberProvider(testDir));
		recordReaderTrain.initialize(testData);
		RecordReaderDataSetIterator testDataIterator = new RecordReaderDataSetIterator(recordReaderTrain, batchSize, 1, 1, true);
		testDataIterator.setPreProcessor(new ImagePreProcessingScaler(0, 1));
		NativeImageLoader imageLoader = new NativeImageLoader();
        CanvasFrame frame = new CanvasFrame("ObjectDetection");
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer yout =
                        (org.deeplearning4j.nn.layers.objdetect.Yolo2OutputLayer)model.getOutputLayer(0);
        testDataIterator.setCollectMetaData(true);
        while (testDataIterator.hasNext()) {
            org.nd4j.linalg.dataset.DataSet ds = testDataIterator.next();
            RecordMetaDataImageURI metadata = (RecordMetaDataImageURI)ds.getExampleMetaData().get(0);
            INDArray features = ds.getFeatures();
            INDArray results = model.outputSingle(features);
            List<DetectedObject> objs = yout.getPredictedObjects(results, detectionThreshold);
            File file = new File(metadata.getURI());
            System.out.println(file.getName() + ": " + objs);

            Mat mat = imageLoader.asMat(features);
            Mat convertedMat = new Mat();
            mat.convertTo(convertedMat, CV_8U, 255, 0);
            int w = metadata.getOrigW();
            int h = metadata.getOrigH();
            Mat image = new Mat();
            resize(convertedMat, image, new Size(w, h));
            System.out.println(objs.size());
            for (DetectedObject obj : objs) {
                double[] xy1 = obj.getTopLeftXY();
                double[] xy2 = obj.getBottomRightXY();
                // String label = labels.get(obj.getPredictedClass());
                int x1 = (int) Math.round(w * xy1[0] / gridWidth);
                int y1 = (int) Math.round(h * xy1[1] / gridHeight);
                int x2 = (int) Math.round(w * xy2[0] / gridWidth);
                int y2 = (int) Math.round(h * xy2[1] / gridHeight);
                rectangle(image, new Point(x1, y1), new Point(x2, y2), Scalar.RED);
                // putText(image, "", new Point(x1 + 2, y2 - 2), FONT_HERSHEY_PLAIN, 1, Scalar.GREEN);
            }
            if(objs.size() >= 1){
	            frame.setTitle(new File(metadata.getURI()).getName() + " - ObjectDetection");
	            frame.setCanvasSize(w, h);
	            frame.showImage(converter.convert(image));
	            frame.waitKey();
            }
        }
        frame.dispose();
	}
	
	private ComputationGraph createModel() throws IOException {
		ComputationGraph pretrained = (ComputationGraph)TinyYOLO.builder().build().initPretrained();
        INDArray priors = Nd4j.create(priorBoxes);

        FineTuneConfiguration fineTuneConf = new FineTuneConfiguration.Builder()
                .seed(seed)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .gradientNormalization(GradientNormalization.RenormalizeL2PerLayer)
                .gradientNormalizationThreshold(1.0)
                .updater(new Adam.Builder().learningRate(learningRate).build())
                .activation(Activation.IDENTITY)
                .trainingWorkspaceMode(WorkspaceMode.SEPARATE)
                .inferenceWorkspaceMode(WorkspaceMode.SEPARATE)
                .build();

        ComputationGraph model = new TransferLearning.GraphBuilder(pretrained)
                .fineTuneConfiguration(fineTuneConf)
                .removeVertexKeepConnections("conv2d_9")
                .removeVertexKeepConnections("outputs")
                .addLayer("convolution2d_9",
                        new ConvolutionLayer.Builder(1,1)
                                .nIn(1024)
                                .nOut(nBoxes * (5 + nClasses))
                                .stride(1,1)
                                .convolutionMode(ConvolutionMode.Same)
                                .weightInit(WeightInit.XAVIER)
                                .activation(Activation.IDENTITY)
                                .build(),
                        "leaky_re_lu_8")
                .addLayer("outputs",
                        new Yolo2OutputLayer.Builder()
                                .lambbaNoObj(lambdaNoObj)
                                .lambdaCoord(lambdaCoord)
                                .boundingBoxPriors(priors)
                                .build(),
                        "convolution2d_9")
                .setOutputs("outputs")
                .build();
        return model;
	}

	/**
	 * 测试文本label
	 * 
	 * @throws InterruptedException
	 */
	public void testTextXY() throws InterruptedException{
    	CanvasFrame frame = new CanvasFrame("ObjectDetection");
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
    	File dir = new File(dirPath + dataSetType);
    	File[] files = dir.listFiles();
    	SAXReader reader = new SAXReader();
    	int splitw = 8;
		for (File file:files) {
			if (!file.getName().endsWith("xml")){
				continue;
			}
			Mat image = imread(dir+"/"+file.getName().replace(".xml", ".jpg"));
			String fileName = file.getName();
			Document document = null;
			try {
				document = reader.read(new File(dir+"/"+fileName));
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			Element root = document.getRootElement();
			Element size = root.element("size");
			Iterator iter =root.elementIterator("object");
			int width = Integer.valueOf(size.element("width").getText());
			int height = Integer.valueOf(size.element("height").getText());
			while (iter.hasNext()){
				Element object = (Element) iter.next();
				String label = object.element("name").getText();
				//System.out.println(label);
				Element robndbox = object.element("bndbox");
				try{
					Integer x1 = Integer.valueOf(robndbox.element("xmin").getText());
					Integer y1 = Integer.valueOf(robndbox.element("ymin").getText());
					Integer x2 = Integer.valueOf(robndbox.element("xmax").getText());
					Integer y2 = Integer.valueOf(robndbox.element("ymax").getText());
					int w = x2 - x1;
					for (int i = 0;i<w/splitw;i++) {
						if (x1+i*splitw > width || x1+(i+1)*splitw > width) {
							continue;
						}
						rectangle(image, new Point(x1+i*splitw, y1), new Point(x1+(i+1)*splitw, y2), Scalar.RED);
		    			//putText(image, String.valueOf(label.charAt(i)), new Point(x1+i*splitw + 2, y2 - 2), FONT_HERSHEY_PLAIN, 1, Scalar.GREEN);
					}
				}catch(Exception e){
					continue;
				}
			}
    		frame.setTitle("file.getName()");
            frame.setCanvasSize(width, height);
            frame.showImage(converter.convert(image));
            frame.waitKey();
		}
    	frame.dispose();
    }
	
	public static class Builder {
		private ObjectDetection objectDetection;
		public Builder() {
			objectDetection = new ObjectDetection();
		}
		public Builder setnClasses(int nClasses) {
			objectDetection.setnClasses(nClasses);
			return this;
		}
		
		public Builder setnEpochs(int nEpochs) {
			objectDetection.setnEpochs(nEpochs);
			return this;
		}
		
		public Builder setBatchSize(int batchSize) {
			objectDetection.setBatchSize(batchSize);
			return this;
		}
		
		public Builder setDataSetType(String dataSetType) {
			objectDetection.setDataSetType(dataSetType);
			return this;
		}
		
		public Builder setDetectionThreshold(double detectionThreshold) {
			objectDetection.setDetectionThreshold(detectionThreshold);
			return this;
		}
		
		public Builder setPriorBoxes(double[][] priorBoxes) {
			objectDetection.setPriorBoxes(priorBoxes);
			return this;
		}
		
		public Builder setLearningRate(double learningRate) {
			objectDetection.setLearningRate(learningRate);
			return this;
		}
		
		public Builder setDirPath(String dirPath) {
			objectDetection.setDirPath(dirPath);
			return this;
		}
		
		public Builder setModelFileName(String modelFileName) {
			objectDetection.setModelFileName(modelFileName);
			return this;
		}
		
		public ObjectDetection toObjectDetection() {
			return objectDetection;
		}
		
	}
}

package com.example.deeplearning.autoencoder;

import java.io.File;
import java.util.List;

import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.DataSetPreProcessor;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;


public class ImageDataSetIterator implements DataSetIterator {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int batchSize = 0;
    private int batchNum = 0;
    private int numExample = 0;
    private ImageLoader load;
    private DataSetPreProcessor preProcessor;

    public ImageDataSetIterator() {
        load = new ImageLoader();
    }
    public ImageDataSetIterator(int batchSize, boolean train, File dir) {
        this.batchSize = batchSize;
        load = new ImageLoader(train, dir);
        numExample = load.totalExamples();
    }

    @Override
    public DataSet next(int i) {
        batchNum += i;
        DataSet ds = load.next(i);
        if (preProcessor != null) {
            preProcessor.preProcess(ds);
        }
        return ds;
    }

    @Override
    public int inputColumns() {
        return 0;
    }

    @Override
    public int totalOutcomes() {
        return 0;
    }

    @Override
    public boolean resetSupported() {
        return false;
    }

    @Override
    public boolean asyncSupported() {
        return false;
    }

    @Override
    public void reset() {
        batchNum = 0;
        load.reset();
    }
    @Override
    public int batch() {
        return batchSize;
    }

    @Override
    public void setPreProcessor(DataSetPreProcessor preProcessor) {
        this.preProcessor = preProcessor;
    }

    @Override
    public DataSetPreProcessor getPreProcessor() {
        return null;
    }

    @Override
    public List<String> getLabels() {
        return null;
    }

    @Override
    public boolean hasNext() {
        if(batchNum < numExample){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public DataSet next() {
        return next(batchSize);
    }
}

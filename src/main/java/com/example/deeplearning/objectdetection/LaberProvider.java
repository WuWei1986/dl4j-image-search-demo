package com.example.deeplearning.objectdetection;

import java.io.File;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.datavec.image.recordreader.objdetect.ImageObject;
import org.datavec.image.recordreader.objdetect.ImageObjectLabelProvider;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * 
 * @author wuwei
 *
 */
public class LaberProvider implements ImageObjectLabelProvider {

	private Map<String, List<ImageObject>> labelMap = new HashMap<String,List<ImageObject>>();
	LaberProvider(File dir){
		DecimalFormat df = new DecimalFormat("#.0");
		List<String> anchors = new ArrayList<>();
		File[] files = dir.listFiles();
		SAXReader reader = new SAXReader();
		int splitw = 8;
		for (File file:files) {
			if (!file.getName().endsWith("xml")){
				continue;
			}
			ArrayList<ImageObject> list = new ArrayList<ImageObject>();
			String fileName = file.getName();
			Document document = null;
			try {
				document = reader.read(new File(dir+"/"+fileName));
			} catch (DocumentException e) {
				e.printStackTrace();
			}
			Element root = document.getRootElement();
			Element size = root.element("size");
			int width = Integer.valueOf(size.element("width").getText());
			int height = Integer.valueOf(size.element("height").getText());
			Iterator<?> iter =root.elementIterator("object");
			if (width == 0 || height == 0) {
				System.out.println("warn："+fileName);
			}
			while (iter.hasNext()){
				Element object = (Element) iter.next();
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
						list.add(new ImageObject(x1+i*splitw, y1, x1+(i+1)*splitw, y2,"T"));
					}
					anchors.add(df.format((double)splitw*13/width)+","+df.format((double)(y2-y1)*13/height));
					
				}catch(Exception e){
					continue;
				}
			}
			labelMap.put(fileName.replace(".xml", ".jpg"), list);
		}
		// 统计所有的真值框
		double[][] _anchors1 = new double[anchors.size()][2];
		double[][] _anchors2 = new double[anchors.size()][2];
		for (int i=0;i<anchors.size();i++){
			double[] box = new double[2];
			box[0] = Double.valueOf(anchors.get(i).split(",")[0]);
			box[1] = Double.valueOf(anchors.get(i).split(",")[1]);
			if(i > 800) {
				_anchors2[i-800] = box;
			}else {
				_anchors1[i] = box;
			}
		}
		System.out.println("");
	}
	
	@Override
    public List<ImageObject> getImageObjectsForPath(String path) {
        File file = new File(path);
        String filename = file.getName();
        return labelMap.get(filename);
    }

    @Override
    public List<ImageObject> getImageObjectsForPath(URI uri) {
        return getImageObjectsForPath(uri.toString());
    }

}

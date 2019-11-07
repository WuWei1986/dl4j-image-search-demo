package com.example.deeplearning.kmeans;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Kmean
{
    // 用来聚类的点集
    public List<Point> points;

    // 将聚类结果保存到文件
    FileWriter out = null;

    // 格式化double类型的输出，保留两位小数
    DecimalFormat dFormat = new DecimalFormat("00.00");

    // 具体执行聚类的对象
    public KMeansCluster kMeansCluster;

    // 簇的数量，迭代次数
    public int numCluster = 5;
    public int numIterator = 200;

    // 点集的数量，生成指定数量的点集
    public int numPoints = 50;

    //聚类结果保存路径
    public static final String RESFILEPATH = Kmean.class.getClassLoader().getResource("")+"/kmeans_res.txt";
    
    public static final String DATAFILEPATH = Kmean.class.getClassLoader().getResource("")+"/data.txt";

    public static void main(String[] args) throws Exception
    {
        //指定点集个数，簇的个数，迭代次数
        Kmean kmeans = new Kmean(50, 5, 100);

        //初始化点集、KMeansCluster对象
        kmeans.init();

        //使用KMeansCluster对象进行聚类
        kmeans.runKmeans();

        kmeans.printRes();
        kmeans.saveResToFile(RESFILEPATH.replace("file:/", ""));
    }

    public Kmean(int numPoints, int cluster_number, int iterrator_number) {

        this.numPoints = numPoints;
        this.numCluster = cluster_number;
        this.numIterator = iterrator_number;
    }

    private void init() throws Exception
    {
        this.initPoints();
        kMeansCluster = new KMeansCluster(numCluster, numIterator, points);
    }

    private void runKmeans()
    {
        kMeansCluster.runKmeans();
    }

    // 初始化点集
    public void initPoints() throws IOException
    {
    	/*
        points = new ArrayList<>(numPoints);

        Point tmpPoint;

        for (int i = 0; i < numPoints; i++)
        {
            tmpPoint = new Point(0d,0d,Math.random() * 150, Math.random() * 100);
            points.add(tmpPoint);
        }
        */
		points = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(DATAFILEPATH.replace("file:/", "")));
		String line = "";
		Point tmpPoint;
		while ((line = br.readLine()) != null) {
			tmpPoint = new Point(0d, 0d, Double.valueOf(line.split(",")[0]), Double.valueOf(line.split(",")[1]));
			points.add(tmpPoint);
		}
		br.close();
    }

    public void printRes()
    {

        System.out.println("==================Centers-I====================");
        for (Point center : kMeansCluster.centers)
        {
            System.out.println(center.toString());
        }

        System.out.println("==================Points====================");

        for (Point point : points)
        {
            System.out.println(point.toString());
        }
    }

    public void saveResToFile(String filePath)
    {
        try
        {
            out = new FileWriter(new File(filePath));

            for (Point point : points)
            {
                out.write(String.valueOf(point.getClusterID()));
                out.write("  ");

                out.write(dFormat.format(point.getW()));
                out.write("  ");
                out.write(dFormat.format(point.getH()));
                out.write("\r\n");
            }

            out.flush();
            out.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}


package com.example.deeplearning.kmeans;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class KMeansCluster
{
    // 聚类中心数
    public int k = 5;

    // 迭代最大次数
    public int maxIter = 50;

    // 测试点集
    public List<Point> points;

    // 中心点
    public List<Point> centers;

    public static final double MINDISTANCE = 1000000.00;

    public KMeansCluster(int k, int maxIter, List<Point> points) {
        this.k = k;
        this.maxIter = maxIter;
        this.points = points;

        //初始化中心点
        initCenters();
    }

    /*
     * 初始化聚类中心
     * 这里的选取策略是，从点集中按序列抽取K个作为初始聚类中心
     * kmeans++
     */
    public void initCenters()
    {
        centers = new ArrayList<>(k);
        Point center = points.get(0);
        center.setClusterID(1);
        centers.add(center);
        while (centers.size() < k) {
	        TreeMap<Double,Point> map = new TreeMap<>();
	        for (int i=0;i<points.size();i++) {
	        	double minDis = 0d;
	        	for (int j=0;j<centers.size();j++){
		        	double dis = 1- box_iou(points.get(i), centers.get(j));
		        	if (j==0) {
		        		minDis = dis;
		        	} else {
			        	if (dis < minDis) {
			        		minDis = dis;
			        	}
		        	}
	        	}
	        	map.put(minDis, points.get(i));
	        }
	        Point newCenter = map.lastEntry().getValue();
	        newCenter.setClusterID(centers.size()+1);
	        centers.add(newCenter);
        }
        /*
        for (int i = 0; i < k; i++)
        {
            Point tmPoint = points.get(i * 2);
            Point center = new Point(0d,0d,tmPoint.getW(), tmPoint.getH());
            center.setClusterID(i + 1);
            centers.add(center);
        }
        */
        System.out.println("");
    }


    /*
     * 停止条件是满足迭代次数
     */
    public void runKmeans()
    {
        // 已迭代次数
        int count = 1;

        while (count++ <= maxIter)
        {
            // 遍历每个点，确定其所属簇
            for (Point point : points)
            {
                assignPointToCluster(point);
            }

            //调整中心点
            adjustCenters();
        }
    }



    /*
     * 调整聚类中心，按照求平衡点的方法获得新的簇心
     */
    public void adjustCenters()
    {
        double sumx[] = new double[k];
        double sumy[] = new double[k];
        int count[] = new int[k];

        // 保存每个簇的横纵坐标之和
        for (int i = 0; i < k; i++)
        {
            sumx[i] = 0.0;
            sumy[i] = 0.0;
            count[i] = 0;
        }

        // 计算每个簇的横纵坐标总和、记录每个簇的个数
        for (Point point : points)
        {
            int clusterID = point.getClusterID();
            // System.out.println(clusterID);
            sumx[clusterID - 1] += point.getW();
            sumy[clusterID - 1] += point.getH();
            count[clusterID - 1]++;
        }

        // 更新簇心坐标
        for (int i = 0; i < k; i++)
        {
            Point tmpPoint = centers.get(i);
            tmpPoint.setW(sumx[i] / count[i]);
            tmpPoint.setH(sumy[i] / count[i]);
            tmpPoint.setClusterID(i + 1);

            centers.set(i, tmpPoint);
        }
    }


    /*划分点到某个簇中，欧式距离标准
     * 对传入的每个点，找到与其最近的簇中心点，将此点加入到簇
     */
    public void assignPointToCluster(Point point)
    {
        double minDistance = MINDISTANCE;

        int clusterID = -1;

        for (Point center : centers)
        {
            //double dis = EurDistance(point, center);
        	double dis = 1- box_iou(point, center);
            if (dis < minDistance)
            {
                minDistance = dis;
                clusterID = center.getClusterID();
            }
        }
        if (clusterID == -1) {
        	System.out.println(centers);
        }
        point.setClusterID(clusterID);

    }

    //欧式距离，计算两点距离
    public double EurDistance(Point point, Point center)
	{
        double detX = point.getW() - center.getW();
        double detY = point.getH() - center.getH();

        return Math.sqrt(detX * detX + detY * detY);
    }
    
    double overlap(double x1, double w1, double x2, double w2)
    {
    	double l1 = x1 - w1/2;
    	double l2 = x2 - w2/2;
    	double left = l1 > l2 ? l1 : l2;
    	double r1 = x1 + w1/2;
    	double r2 = x2 + w2/2;
    	double right = r1 < r2 ? r1 : r2;
        return right - left;
    }

    double box_intersection(Point a, Point b)
    {
    	double w = overlap(a.getX(), a.getW(), b.getX(), b.getW());
    	double h = overlap(a.getY(), a.getH(), b.getY(), b.getH());
        if(w < 0 || h < 0) return 0;
        double area = w*h;
        return area;
    }

    double box_union(Point a, Point b)
    {
    	double i = box_intersection(a, b);
    	double u = a.getW()*a.getH() + b.getW()*b.getH() - i;
        return u;
    }
    double box_iou(Point a, Point b)
    {
        return box_intersection(a, b)/box_union(a, b);
    }

}
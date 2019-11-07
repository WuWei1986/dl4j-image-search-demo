package com.example.deeplearning.kmeans;

public class Point
{
    // 点的坐标
    private Double x;
    private Double y;
    private Double w;
    private Double h;

    // 所在类ID
    private int clusterID = -1;

    public Point(Double x, Double y,Double w, Double h) {

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    @Override
    public String toString()
    {
        return String.valueOf(getClusterID()) + " " + String.valueOf(this.x) + " " + String.valueOf(this.y)+ " " + String.valueOf(this.w) + " " + String.valueOf(this.h);
    }

    public Double getX()
    {
        return x;
    }

    public void setX(Double x)
    {
        this.x = x;
    }

    public Double getY()
    {
        return y;
    }

    public void setY(Double y)
    {
        this.y = y;
    }

    public Double getW() {
		return w;
	}

	public void setW(Double w) {
		this.w = w;
	}

	public Double getH() {
		return h;
	}

	public void setH(Double h) {
		this.h = h;
	}

	public int getClusterID()
    {
        return clusterID;
    }

    public void setClusterID(int clusterID)
    {
        this.clusterID = clusterID;
    }
}


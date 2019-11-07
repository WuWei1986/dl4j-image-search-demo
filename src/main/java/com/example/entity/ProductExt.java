package com.example.entity;

import java.io.Serializable;

public class ProductExt extends Product implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String predictText;
	private String phash;
	private String createTime;
	private float[] features;
	public String getPredictText() {
		return predictText;
	}
	public void setPredictText(String predictText) {
		this.predictText = predictText;
	}
	public String getPhash() {
		return phash;
	}
	public void setPhash(String phash) {
		this.phash = phash;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public float[] getFeatures() {
		return features;
	}
	public void setFeatures(float[] features) {
		this.features = features;
	}
	@Override
	public String toString() {
		return "ProductExt [id=" + id + ", predictText=" + predictText + ", phash=" + phash + ", createTime="
				+ createTime + ", getTitle()=" + getTitle() + ", getImgUrl()=" + getImgUrl() + ", getPrice()="
				+ getPrice() + ", getSourceId()=" + getSourceId() + ", getState()=" + getState() + "]";
	}
	
	
	
}

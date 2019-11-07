package com.example.core;

import java.io.Serializable;

public class SessionObject implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Object object;
	
	private Integer timestamp;
	
	public SessionObject(Object object,Integer timestamp){
		this.object = object;
		this.timestamp = timestamp;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Integer getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Integer timestamp) {
		this.timestamp = timestamp;
	} 
	
	

}

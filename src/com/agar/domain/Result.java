package com.agar.domain;

import java.io.Serializable;

public class Result<T> implements Serializable{
	private int status;
	private T data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public T getData() {
		return data;
	}

	public void setData(T t) {
		this.data = t;
	}

}

package com.danh.bloombergopenapi;

public class SpinnerModel {

	private String requestName = "";
	private String image = "";
	private String url = "";

	/*********** Set Methods ******************/
	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/*********** Get Methods ****************/
	public String getRequestName() {
		return this.requestName;
	}

	public String getImage() {
		return this.image;
	}

	public String getUrl() {
		return this.url;
	}
}
